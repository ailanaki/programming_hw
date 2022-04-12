package info.kgeorgiy.ja.yakupova.concurrent;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class ParallelMapperImpl implements ParallelMapper {
    private ArrayList<Thread> threadList;
    private WorkerQueue queue;


    public ParallelMapperImpl(int threads) {
        queue = new WorkerQueue();
        threadList = new ArrayList<>();
        // :NOTE: one runnable instance
        for (int i = 0; i < threads; i++) {
            threadList.add(new mapperThread());
            threadList.get(i).start();
        }

    }

    private class mapperThread extends Thread{
        @Override
        public void run() {
            while (true) {
                try {
                    Worker<?, ?> worker = queue.get();
                    if (worker != null) {
                        worker.run();
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

    private static class WorkerQueue {
        private LinkedList<Worker<?, ?>> queue;

        public WorkerQueue() {
            queue = new LinkedList<>();
        }

        public synchronized Worker<?, ?> get() throws InterruptedException {
            while (queue.isEmpty()) {
                wait();
            }
            return queue.pollFirst();

        }

        public synchronized void set(Worker<?, ?> help) {
            queue.add(help);
            notifyAll();
        }
    }

    private static class Worker<T, R> implements Runnable {
        Function<? super T, ? extends R> f;
        T arg;
        R result;
        boolean completed = false;

        public Worker(Function<? super T, ? extends R> f, T arg) {
            this.f = f;
            this.arg = arg;
        }

        @Override
        public synchronized void run() {
            result = f.apply(arg);
            completed = true;
            notifyAll();
        }

        synchronized R getResult() throws InterruptedException {
            while (!completed) {
                wait();
            }
            return result;
        }
    }


    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException {
        ArrayList<R> results = new ArrayList<>();
        ArrayList<Worker<T, R>> worker = new ArrayList<>();
        for (T arg : args) {
            Worker<T, R> help = new Worker<>(f, arg);
            worker.add(help);
            queue.set(help);
        }

        for (Worker<T, R> work : worker) {
            results.add(work.getResult());
        }
        return results;
    }

    @Override
    public void close(){
        for (Thread thread : threadList) {
            thread.interrupt();
        }
        for (Thread thread: threadList) {
            if (thread.isAlive()){
                try {
                    thread.join();
                }catch (InterruptedException ignored){}
            }
        }
        // :NOTE: wait for execution
    }
}

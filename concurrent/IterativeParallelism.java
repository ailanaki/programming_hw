package info.kgeorgiy.ja.yakupova.concurrent;

import info.kgeorgiy.java.advanced.concurrent.ListIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class IterativeParallelism implements ListIP {
    private ParallelMapperImpl mapper = null;
    private boolean fromMapper = false;

    public IterativeParallelism(ParallelMapper mapper) {
        this.mapper = (ParallelMapperImpl) mapper;
        this.fromMapper = true;
    }

    public IterativeParallelism() {
    }
    private <T> ArrayList<List<? extends T>> split(int threads, List<? extends T> values) {
        int[] prelist = new int[threads];
        for (int i = 0; i < values.size(); i++) {
            prelist[i % threads] = prelist[i % threads] + 1;
        }
        for (int i = 1; i < threads; i++) {
            prelist[i] += prelist[i - 1];
        }
        ArrayList<List<? extends T>> args = new ArrayList<>();
        args.add(values.subList(0, prelist[0]));
        for (int i = 1; i < threads; i++) {
            args.add(values.subList(prelist[i - 1],prelist[i]));
        }
        return args;
    }

    private <T> List<T> joinElem(List<List<T>> preAnswer) {
        List<T> answer = new ArrayList<>();
        for (List<T> list : preAnswer) {
            answer.addAll(list);
        }
        return answer;
    }

    private static class Worker<T, R> {
        R object;

        R work(int threads, List<? extends T> values, Function<List<? extends T>, ? extends R> f, BiFunction<R, R, Boolean> correct, R elem) throws InterruptedException {
            ArrayList<Thread> threadList = new ArrayList<>();
            object = elem;
            for (int i = 0; i < threads; i++) {
                int finalI = i;
                threadList.add(new Thread(() -> {
                    R now = f.apply(values.subList(finalI * values.size() / threads,
                            (finalI + 1) * values.size() / threads));
                    if (correct.apply(object, now)) {
                        synchronized (this) {
                            if (correct.apply(object, now)) {
                                object = now;
                            }
                        }
                    }
                }
                ));
                threadList.get(i).start();
            }
            for (Thread thread : threadList) {
                if (thread.isAlive()) {
                    thread.join();
                }
            }
            return object;
        }
    }

    private <T, R> R makeWork(int threads, List<? extends T> values, Function<List<? extends T>, ? extends R> f,
                              BiFunction<R, R, Boolean> correct, R elem, Function<List<? extends R>, ? extends R> endFunc) throws InterruptedException {
        if (fromMapper) {
            List<R> answer = mapper.map(f, split(threads, values));
            return endFunc.apply(answer);
        } else {
            return new Worker<T, R>().work(threads, values, f, correct, elem);
        }


    }

    @Override
    public <T> T maximum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return makeWork(threads, values,
                (x) -> x.stream().max(comparator).orElse(null),
                (x, y) -> (y != null) && (comparator.compare(y, x) > 0),
                values.get(0),
                (x) -> x.stream().filter(Objects::nonNull).max(comparator).orElse(null));
    }

    @Override
    public <T> T minimum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return maximum(threads, values, comparator.reversed());
    }

    @Override
    public <T> boolean any(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return makeWork(threads, values,
                (x) -> x.stream().anyMatch(predicate),
                (x, y) -> (y),
                false,
                (x) -> x.stream().anyMatch((y) -> y));
    }

    @Override
    public <T> boolean all(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return !any(threads, values, predicate.negate());
    }

    @Override
    public String join(int threads, List<?> values) throws InterruptedException {
        List<String> preAnswer = mapper.map(x -> x.stream()
                .map(Object::toString)
                .collect(Collectors.joining()), split(threads, values));
        return String.join("", preAnswer);
    }

    @Override
    public <T> List<T> filter(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        List<List<T>> preAnswer = mapper.map(
                x -> x.stream()
                        .filter(predicate)
                        .collect(Collectors.toList()), split(threads, values));
        return joinElem(preAnswer);
    }

    @Override
    public <T, U> List<U> map(int threads, List<? extends T> values, Function<? super T, ? extends U> f) throws InterruptedException {
        List<List<U>> preAnswer = mapper.map(
                x -> x.stream()
                        .map(f)
                        .collect(Collectors.toList()), split(threads, values));
        return joinElem(preAnswer);
    }
}

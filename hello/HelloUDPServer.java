package info.kgeorgiy.ja.yakupova.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HelloUDPServer implements HelloServer {
    final public int TIMEOUT_MILLS = 1000;
    private ExecutorService worker;
    private DatagramSocket socket;

    @Override
    public void start(int port, int threads) {
        worker = Executors.newFixedThreadPool(threads);

        try {
            socket = new DatagramSocket(port);
            socket.setSoTimeout(TIMEOUT_MILLS);
            for (int i = 0; i < threads; i++) {
                worker.submit(() -> {
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            byte[] bytes = new byte[socket.getReceiveBufferSize()];
                            DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
                            if (socket.isClosed()) {
                                break;
                            }
                            socket.receive(packet);
                            String received = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
                            byte[] newBuf = ("Hello, " + received).getBytes();
                            packet.setData(newBuf);
                            socket.send(packet);
                        } catch (IOException ignored) {
                        }
                    }
                });
            }

        } catch (SocketException e) {
            System.err.println("Socket exception. Cause: " + e.getCause().getMessage());
        }
    }

    @Override
    public void close() {
        socket.close();
        HelloUtils.stop(worker);
    }


}

package info.kgeorgiy.ja.yakupova.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public class HelloUDPClient implements HelloClient {
    final public int TIMEOUT_MILLS = 100;

    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        SocketAddress address = HelloUtils.createSocketAddress(host,port);
        ExecutorService worker = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            int finalI = i;
            worker.submit(() -> {
                try (DatagramSocket socket = new DatagramSocket()) {
                    socket.setSoTimeout(TIMEOUT_MILLS); // :NOTE: move to a const value
                    String message;
                    String responce;
                    byte[] request;
                    for (int j = 0; j < requests; j++) {
                        if (socket.isClosed()) {
                            break;
                        }
                        message = prefix + finalI + "_" + j;
                        responce = "Hello, " + message;
                        request = message.getBytes(StandardCharsets.UTF_8);
                        System.out.println("Request: " + message);
                        while (!socket.isClosed() &&! Thread.currentThread().isInterrupted()) {
                            try {
                                socket.send(new DatagramPacket(request, request.length, address));
                                // :NOTE: const size buffer
                                byte[] bytes = new byte[socket.getReceiveBufferSize()];
                                DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
                                socket.receive(packet);
                                String received = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
                                if (responce.equals(received)) {
                                    break;
                                }
                            } catch (IOException ignored) {
                            }
                        }
                           System.out.println("Responce: "+ responce);
                    }

                } catch (SocketException e) {
                    System.out.println("Don't work, socket");
                }
            });
        }
        HelloUtils.stop(worker);
    }
}

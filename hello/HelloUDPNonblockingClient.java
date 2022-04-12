package info.kgeorgiy.ja.yakupova.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;

public class HelloUDPNonblockingClient implements HelloClient {
    final public int TIMEOUT_MILLS = 100;
    final public int BUFFER_SIZE = 1024;

    private static class Helper {
        int threads, requests;
        String prefix;

        public Helper(int threads, int requests, String prefix) {
            this.threads = threads;
            this.requests = requests;
            this.prefix = prefix;
        }

        public String getMessage() {
            return prefix + threads + "_" + requests;
        }
    }

    void makeRequest(SelectionKey key, SocketAddress address) throws IOException {
        Helper helper = (Helper) key.attachment();
        String message = helper.getMessage();
        ByteBuffer request = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
        ((DatagramChannel) key.channel()).send(request, address);
        key.interestOps(SelectionKey.OP_READ);
    }


    void collectResponse(SelectionKey key, ByteBuffer buff, int requests) throws IOException {
        buff.clear();
        Helper helper = (Helper) key.attachment();
        DatagramChannel channel = (DatagramChannel) key.channel();
        channel.read(buff);
        String response = HelloUtils.bufferToResponse(buff);
        String message = helper.getMessage();
        if (response.equals("Hello, " + message)) {
            System.out.println("Request: " + message);
            System.out.println("Response: " + response);
            helper.requests++;
            if (helper.requests + 1 > requests) {
                channel.close();
                return;
            }
        }
        key.interestOps(SelectionKey.OP_WRITE);

    }

    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        SocketAddress address = HelloUtils.createSocketAddress(host, port);
        try (Selector selector = Selector.open()) {
            for (int i = 0; i < threads; i++) {
                Helper helper = new Helper(i, 0, prefix);
                DatagramChannel channel = HelloUtils.createDatagramChannel();
                channel.connect(address);
                channel.register(selector, SelectionKey.OP_WRITE, helper);
            }
            while (!selector.keys().isEmpty()) {
                selector.select(TIMEOUT_MILLS);
                if (selector.selectedKeys().isEmpty()) {
                    for (SelectionKey key : selector.keys()) {
                        if (key.isWritable()) {
                            makeRequest(key, address);
                        }
                    }
                }
                ByteBuffer buff = ByteBuffer.allocate(BUFFER_SIZE);
                for (SelectionKey key : selector.selectedKeys()) {
                    if (key.isWritable()) {
                        makeRequest(key, address);
                    } else if (key.isReadable()) {
                        collectResponse(key, buff, requests);
                    }
                }
                selector.selectedKeys().clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

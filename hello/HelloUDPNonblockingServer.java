package info.kgeorgiy.ja.yakupova.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class HelloUDPNonblockingServer implements HelloServer {
    final public int TIMEOUT_MILLS = 100;


    private DatagramChannel channel;
    private Selector selector;
    private ExecutorService socketThread;
    private ExecutorService responsesThreads;
    private ConcurrentLinkedQueue<Response> responses;

    private static class Response {
        ByteBuffer buffer;
        SocketAddress address;
        DatagramChannel channel;

        public Response(ByteBuffer buffer, SocketAddress address, DatagramChannel channel) {
            this.buffer = buffer;
            this.address = address;
            this.channel = channel;
        }
    }


    @Override
    public void start(final int port, final int threads) {
        responses = new ConcurrentLinkedQueue<>();
        responsesThreads = Executors.newFixedThreadPool(threads);
        socketThread = Executors.newSingleThreadExecutor();
        // :NOTE: Один поток
        try {
            selector = Selector.open();
            channel = HelloUtils.createDatagramChannel();
            channel.bind(new InetSocketAddress(port));
            channel.register(selector, SelectionKey.OP_READ);

            socketThread.submit(() -> {
                while (!channel.socket().isClosed() && !Thread.interrupted()) {
                    try {
                        while (!responses.isEmpty()) {
                            Response now = responses.poll();
                            now.channel.send(now.buffer, now.address);
                        }
                        selector.select(TIMEOUT_MILLS);
                        if (!selector.selectedKeys().isEmpty()) {
                            final Iterator<SelectionKey> keyIt = selector.selectedKeys().iterator();
                            final SelectionKey key = keyIt.next();
                            keyIt.remove();
                            final DatagramChannel channel = (DatagramChannel) key.channel();
                            final ByteBuffer buffer = ByteBuffer.allocate(channel.socket().getReceiveBufferSize());
                            buffer.clear();
                            final SocketAddress address = channel.receive(buffer);
                            responsesThreads.submit(() -> makeResponse(buffer, address, channel));
                        }
                    } catch (final IOException ignored) {
                    }
                }
            });
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private void makeResponse(ByteBuffer buffer, SocketAddress address, DatagramChannel channel) {
        final String received = HelloUtils.bufferToResponse(buffer);
        responses.add(new Response(ByteBuffer.wrap(("Hello, " + received).getBytes(StandardCharsets.UTF_8)),
                address, channel));
    }


    @Override
    public void close() {
        try {
            HelloUtils.stop(socketThread);
            HelloUtils.stop(responsesThreads);
            channel.close();
            selector.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }

    }
}

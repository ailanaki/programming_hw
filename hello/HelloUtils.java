package info.kgeorgiy.ja.yakupova.hello;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class HelloUtils {

    static void stop(ExecutorService worker) {
        worker.shutdown();
        try {
            if (!worker.awaitTermination(1, TimeUnit.SECONDS)) {
                worker.shutdownNow();
                if (!worker.awaitTermination(1, TimeUnit.SECONDS))
                    System.err.println("Can't shutdown pool");
            }
        } catch (InterruptedException ie) {
            worker.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    static DatagramChannel createDatagramChannel() throws IOException {
        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        return channel;
    }

    static String bufferToResponse(ByteBuffer buffer) {
        buffer.flip();
        return StandardCharsets.UTF_8.decode(buffer).toString();
    }

    static SocketAddress createSocketAddress(String host, int port) {
        try {
            return new InetSocketAddress(InetAddress.getByName(host), port);
        } catch (UnknownHostException e) {
            System.err.println("Unknown host");
            return null;
        }
    }
}

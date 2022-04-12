package info.kgeorgiy.ja.yakupova.crawler;

import info.kgeorgiy.java.advanced.crawler.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class WebCrawler implements Crawler {

    private Downloader downloader;
    private ExecutorService downloadPool, extractorPool;

    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        this.downloader = downloader;
        this.downloadPool = Executors.newFixedThreadPool(downloaders);
        this.extractorPool = Executors.newFixedThreadPool(extractors);
    }


    @Override
    public Result download(String url, int depth) {
        List<String> startList = new ArrayList<>();
        List<String> result = new ArrayList<>();
        Map<String, IOException> IOMap = new HashMap<>();
        startList.add(url);
        bfs(result, IOMap, startList, depth, new HashSet<>());
        return new Result(result, IOMap);
    }

    private void bfs(List<String> result, Map<String, IOException> IOMap, List<String> links, int depth, Set<String> done) {
        Map<String, Future<Document>> documents = new HashMap<>();
        List<Future<List<String>>> urls = new ArrayList<>();
        for (String url : links) {
            if (!done.add(url)) {
                continue;
            }
            documents.put(url, downloadPool.submit(() -> downloader.download(url)));
        }
        // :NOTE: you can save an extra call to documents.get by using entryset
        for (String url : documents.keySet()) {
            try {
                Document resDoc = documents.get(url).get();
                if (depth != 1) {
                    // you download A and B
                    // you wait for A, the whole extractorPool awaits
                    // :NOTE: not parallel enough
                    urls.add(extractorPool.submit(resDoc::extractLinks));
                }
                result.add(url);
            } catch (InterruptedException ignored) {
            } catch (ExecutionException e) {
                IOMap.put(url, (IOException) e.getCause());
            }
        }
        if (depth != 1) {
            List<String> nextLinks = urls.stream().flatMap(x -> {
                try {
                    return x.get().stream();
                } catch (InterruptedException | ExecutionException ignored) {
                    return null;
                }
            }).collect(Collectors.toList());

            bfs(result, IOMap, nextLinks, depth - 1, done);
        }
    }

    @Override
    public void close() {
        shutdown(downloadPool);
        shutdown(extractorPool);
    }

    private void shutdown(ExecutorService pool){
        pool.shutdown();
        try {
            if (!pool.awaitTermination(1, TimeUnit.MINUTES)) {
                pool.shutdownNow();
                if (!pool.awaitTermination(1, TimeUnit.MINUTES))
                    System.err.println("Can't shutdown pool");
            }
        } catch (InterruptedException ie) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static int parse(String str) {
        StringBuilder res = new StringBuilder();
        int i = 0;
        while (i < str.length() && !Character.isWhitespace(str.charAt(i))) {
            if (str.charAt(i) != '[' && str.charAt(i) != ']') {
                res.append(str.charAt(i));
            }
            i++;
        }
        return Integer.parseInt(res.toString());
    }

    public static void main(String[] args) {
        int depth = parse(args[2]);
        int downloads = parse(args[3]);
        int extractors = parse(args[4]);
        int perHost = parse(args[5]);
        try {
            WebCrawler crawler = new WebCrawler(new CachingDownloader(), downloads, extractors, perHost);
            crawler.download(args[1], depth);
            // :NOTE: make sure to actually release resources
            crawler.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

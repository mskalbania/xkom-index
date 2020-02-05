package com.xkom.engine;

import io.vavr.CheckedFunction0;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import io.vavr.concurrent.Future;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@Component
public class HttpClientWrapper {

    private static final int BASE_DELAY = 500;

    private final int timeout;
    private final int delay;
    private final int maxRecursiveCalls;

    private final Logger logger;
    private final ExecutorService executorService;
    private final AtomicInteger callCounter = new AtomicInteger(0);

    public HttpClientWrapper(@Value("${http.client.timeout}") int timeout,
                             @Value("${http.client.delay}") int delay,
                             @Value("${http.client.threads}") int threads,
                             @Value("${app.max.recursive.calls}") int maxRecursiveCalls,
                             Logger logger) {
        this.logger = logger;
        this.timeout = timeout;
        this.delay = delay;
        this.maxRecursiveCalls = maxRecursiveCalls;
        this.executorService = Executors.newFixedThreadPool(threads);
    }

    public <T> Future<T> async(CheckedFunction0<T> action) {
        return Future.of(executorService, action);
    }

    public Document get(String url) throws Exception {
        Thread.sleep(new Random().nextInt(delay) + BASE_DELAY);
        logger.debug("Calling http [{}], [{}]", url, callCounter.incrementAndGet());
        return Jsoup.connect(url)
                    .timeout(timeout)
                    .get();
    }

    public <T> Set<T> getRecursively(List<String> urls, Function<Document, List<T>> documentExtractor) {
        return retryRecursively(HashSet.empty(), urls, 1, documentExtractor);
    }

    private <T> Set<T> retryRecursively(Set<T> previous, List<String> currentUrls, int iterationNumber,
                                        Function<Document, List<T>> extractor) {
        if (currentUrls.isEmpty() || iterationNumber == maxRecursiveCalls) { //END OF RECURSIVE CALLS
            return previous;
        }
        logger.info("Expected calls : {}, Iteration : {}", currentUrls.length(), iterationNumber);

        List<Future<Either<String, List<T>>>> futureCalls = currentUrls.map(url -> async(callAndExtract(url, extractor)));
        List<Either<String, List<T>>> callResults = futureCalls.map(ft -> ft.await(10, TimeUnit.MINUTES))
                                                               .map(Future::get);

        List<T> retrieved = callResults.filter(Either::isRight).flatMap(Either::get);
        List<String> failed = callResults.filter(Either::isLeft).map(Either::getLeft);

        logger.info("Collected : {}, Failed : {}", retrieved.length(), failed.length());

        return retryRecursively(previous.addAll(retrieved), failed, iterationNumber + 1, extractor);
    }

    private <T> CheckedFunction0<Either<String, List<T>>> callAndExtract(String url, Function<Document, List<T>> extractor) {
        return () -> Try.of(() -> get(url))
                        .map(extractor)
                        .onFailure(Failures.unableToRetrieveProductList(url, logger))
                        .toEither(url);
    }
}

package com.xkom.engine;

import com.xkom.model.Product;
import com.xkom.service.ProductService;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;

import static org.apache.commons.lang3.time.DurationFormatUtils.formatDuration;

@Component
public class IndexingEngine {

    private static final String VERSION = "";

    private final Logger logger;
    private final DocumentParser documentParser;
    private final HttpClientWrapper client;
    private final ProductService productService;

    public IndexingEngine(Logger logger, DocumentParser documentParser,
                          HttpClientWrapper client, ProductService productService) {
        this.logger = logger;
        this.documentParser = documentParser;
        this.client = client;
        this.productService = productService;
    }

    public void index() {
        Option<Document> document = Try.of(() -> Jsoup.connect(documentParser.getStartingUrl()).get())
                                       .onFailure(Failures.connectionFailed(logger, documentParser.getBaseUrl()))
                                       .toOption();

        //TODO FIGURE OUT VERSIONING
//        if (document.isDefined()) {
//            String obtainedVersion = documentParser.version(document.get());
//            logger.info("Provider [{}] supported version is - {}, retrieved version is - {}",
//                        documentParser.getBaseUrl(), VERSION, obtainedVersion);
//            if (!VERSION.equalsIgnoreCase(obtainedVersion)) {
//                logger.error("Versions does not match, shutting down");
//                return;
//            }
//        }

        logger.info("Indexing started...");
        LocalTime start = LocalTime.now();

        //noinspection Convert2MethodRef
        List<String> categoriesUrls = document.map(documentParser::categoriesUrls)
                                              .toList()
                                              .flatMap(li -> List.ofAll(li) /*cannot inf*/);

        logger.info("Scanning all product urls...");
        Set<String> productUrls = client.getRecursively(categoriesUrls, documentParser::productsUrls)
                                        .map(url -> documentParser.getBaseUrl() + url);

        logger.info("Scanning completed - took [{}] | DISTINCT products found [{}]",
                    formatDuration(Duration.between(start, LocalTime.now()).toMillis(), "mm:ss", false),
                    productUrls.length());

        logger.info("Scanning product pages...");
        Set<Product> products = client.getRecursively(productUrls.toList(), documentParser::product);

        logger.info("Scanning completed - took [{}] | products scanned [{}]",
                    formatDuration(Duration.between(start, LocalTime.now()).toMillis(), "mm:ss", false),
                    productUrls.length());

        productService.storeAll(products);
    }
}

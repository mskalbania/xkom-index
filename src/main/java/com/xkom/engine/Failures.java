package com.xkom.engine;

import org.slf4j.Logger;

import java.util.function.Consumer;

public class Failures {

    public static Consumer<Throwable> unableToConnectToServer(Logger logger) {
        return ex -> logger.error("Error while connecting to server: [{}]", ex.getMessage());
    }

    public static Consumer<Throwable> unableToRetrieveCategoryList(Logger logger) {
        return ex -> logger.error("Error while retrieving category list [{}]", ex.getMessage());
    }

    public static Consumer<Throwable> unableToParseCategoryElement(Logger logger) {
        return ex -> logger.error("Error while parsing category element [{}]", ex.getMessage());
    }

    public static Consumer<Throwable> unableToRetrieveProductList(String url, Logger logger) {
        //Debug - Common error, in recursive calls does not affect output
        return ex -> logger.debug("Error while retrieving product list [{}] at [{}]", ex.getMessage(), url);
    }

    public static Consumer<Throwable> unableToParseProduct(Logger logger) {
        return ex -> logger.error("Error while retrieving product list [{}]", ex.getMessage());
    }

    public static Consumer<Throwable> unableToStorePrice(Logger logger) {
        return ex -> logger.error("Error while storing price for products [{}]", ex.getMessage());
    }

    public static Consumer<Throwable> unableToStoreProducts(Logger logger) {
        return ex -> logger.error("Error while storing products [{}]", ex.getMessage());
    }

    public static Consumer<Throwable> connectionFailed(Logger logger, String provider) {
        return ex -> logger.error("Unable to connect to provider - {}", provider);
    }
}

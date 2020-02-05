package com.xkom.engine;

import com.xkom.model.Product;
import io.vavr.collection.List;
import org.jsoup.nodes.Document;

public interface DocumentParser {

    List<String> categoriesUrls(Document document);

    List<String> productsUrls(Document document);

    String version(Document document);

    List<Product> product(Document document);

    String getStartingUrl();

    String getBaseUrl();
}

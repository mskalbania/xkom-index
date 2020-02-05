package com.xkom.engine.parser.xkom;

import com.xkom.engine.DocumentParser;
import com.xkom.engine.Failures;
import com.xkom.model.Product;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Iterator;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class XKomParser implements DocumentParser { //TODO IMPLEMENT VERSION CHECK!! && FAILURES UPDATE!!

    private static final String STARTING_URL = "https://x-kom.pl/g/2-laptopy-i-tablety.html";
    private static final String X_KOM_BASE_URL = "https://x-kom.pl";

    private static final String PAGE_CONST = "?page=";
    private static final int ELEMENTS_PER_SITE = 30;

    private static final String CATEGORY_LIST = "simple-category-navigation";
    private static final String PRODUCT_CLASS = "sc-1yu46qn-5";
    private static final String PRODUCT_URL_CLASS = ".sc-4ttund-0.kTcxmb";

    private final Logger logger;
    private final ProductParser productParser;

    public XKomParser(Logger logger, ProductParser productParser) {
        this.logger = logger;
        this.productParser = productParser;
    }

    @Override
    public List<String> categoriesUrls(Document document) {
        return Try.of(() -> parseCategoriesUrls(document))
                  .toOption()
                  .getOrElse(List::empty)
                  .flatMap(this::generateUrls);
    }

    @Override
    public String version(Document document) {
        return "";
    }

    @Override
    public List<String> productsUrls(Document document) {
        return Try.of(() -> document.getElementsByClass(PRODUCT_CLASS))
                  .onFailure(Failures.unableToParseProduct(logger))
                  .toList()
                  .flatMap(List::ofAll)
                  .flatMap(this::parseProductUrl)
                  .distinct();
    }

    @Override
    public List<Product> product(Document document) {
        return productParser.parse(document).toList();
    }

    @Override
    public String getStartingUrl() {
        return STARTING_URL;
    }

    @Override
    public String getBaseUrl() {
        return X_KOM_BASE_URL;
    }

    private List<Tuple2<String, Integer>> parseCategoriesUrls(Document document) {
        return List.ofAll(document.getElementsByClass(CATEGORY_LIST))
                   .flatMap(this::extract);
    }

    private List<String> generateUrls(Tuple2<String, Integer> categoryInfo) {
        return Iterator.iterate(1, siteNum -> siteNum + 1)
                       .takeWhile(siteNum -> siteNum <= (categoryInfo._2 / ELEMENTS_PER_SITE) + 1)
                       .map(siteNum -> constructCategoryUrl(categoryInfo._1, siteNum))
                       .toList();
    }

    private String constructCategoryUrl(String category, int siteNumber) {
        return X_KOM_BASE_URL + category + PAGE_CONST + siteNumber;
    }

    private Option<String> parseProductUrl(Element element) {
        return Try.of(() -> element.select(PRODUCT_URL_CLASS))
                  .toList()
                  .flatMap(List::ofAll)
                  .find(e -> e.hasAttr("href"))
                  .map(e -> e.attr("href"));
    }

    private List<Tuple2<String, Integer>> extract(Element rootElement) {
        return Try.of(() -> rootElement.child(1))
                  .onFailure(Failures.unableToRetrieveCategoryList(logger))
                  .toList()
                  .flatMap(Element::children)
                  .flatMap(this::extractCategoryUrls);
    }

    private List<Tuple2<String, Integer>> extractCategoryUrls(Element categoryElement) {
        return Try.of(() -> categoryElement.children().get(2).children()) //traversing tree to single <a>
                  .onFailure(Failures.unableToRetrieveCategoryList(logger))
                  .toList()
                  .flatMap(List::ofAll)
                  .flatMap(this::extractCategoryUrl);
    }

    private Option<Tuple2<String, Integer>> extractCategoryUrl(Element element) {
        return Try.of(() -> Tuple.of(url(element), amount(element)))
                  .onFailure(Failures.unableToParseCategoryElement(logger))
                  .toOption();
    }

    private String url(Element element) {
        return element.child(0).attr("href");
    }

    private Integer amount(Element element) {
        String spanTxt = element.child(0).child(0).text();
        return Integer.parseInt(spanTxt.substring(1, spanTxt.length() - 1));
    }
}

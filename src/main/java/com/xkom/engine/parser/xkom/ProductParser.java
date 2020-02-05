package com.xkom.engine.parser.xkom;

import com.xkom.model.Price;
import com.xkom.model.Product;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.function.Function;

import static java.util.function.Function.identity;

@Component
public class ProductParser {

    private static final String ID_CSS = ".sc-1x6crnh-4";
    private static final String NAME_CSS = ".sc-1x6crnh-5";
    private static final String PRICE_CSS = ".u7xnnm-4";

    private final Logger log;

    public ProductParser(Logger log) {
        this.log = log;
    }

    public Option<Product> parse(Document document) {
        Option<String> id = id(document);
        Option<String> name = name(document);
        Option<Price> price = price(document);
        if (id.isEmpty() || name.isEmpty() || price.isEmpty()) {
            log.warn("Unable to create some product element - values : [{},{},{}]", id, name, price);
            return Option.none();
        }
        Product product = Product.builder()
                                 .name(name.get())
                                 .price(List.of(price.get()))
                                 .providerId(id.get())
                                 .url(document.location())
                                 .build();

        log.debug("Product created - {}", product);
        return Option.some(product);
    }

    private Option<String> id(Document document) {
        return Try.of(() -> document.select(ID_CSS).text())
                  .map(String::trim)
                  .map(s -> Arrays.asList(s.split(" ")))
                  .toList()
                  .flatMap(List::ofAll)
                  .lastOption()
                  .filter(it -> it.matches("[0-9]+")); //to ensure that correct value was picked
    }

    private Option<String> name(Document document) {
        return trySelecting(NAME_CSS, document, identity());
    }

    private Option<Price> price(Document document) {
        return trySelecting(PRICE_CSS, document, this::priceMapper);
    }

    private Price priceMapper(String priceTxt) {
        BigDecimal price = new BigDecimal(priceTxt.substring(0, priceTxt.length() - 3)
                                                  .replace(",", ".")
                                                  .replace(" ", ""));
        return Price.create(price, LocalDateTime.now());
    }

    private <T> Option<T> trySelecting(String selector, Document document, Function<String, T> mapper) {
        return Try.of(() -> document.select(selector))
                  .map(Elements::text)
                  .map(mapper)
                  .toOption();
    }
}

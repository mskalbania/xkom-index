package com.xkom.api;

import com.xkom.engine.IndexingEngine;
import com.xkom.entity.PriceEntity;
import com.xkom.entity.ProductEntity;
import com.xkom.model.Price;
import com.xkom.model.Product;
import com.xkom.repository.PriceRepository;
import com.xkom.repository.ProductRepository;
import com.xkom.service.ProductService;
import io.vavr.Tuple;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;

//TODO might switch to 'admin api' when security enabled
@RestController
@RequestMapping("/debug-api/")
public class DebugApi {

    private final IndexingEngine engine;
    private final ProductRepository productRepository;
    private final PriceRepository priceRepository;
    private final ProductService productService;

    public DebugApi(IndexingEngine engine, ProductRepository productRepository, PriceRepository priceRepository, ProductService productService) {
        this.engine = engine;
        this.productRepository = productRepository;
        this.priceRepository = priceRepository;
        this.productService = productService;
    }

    @GetMapping("/test")
    public ResponseEntity<List<String>> test() {
        Product p = Product.builder()
                           .url("asdas")
                           .providerId("asdasd")
                           .name("asdasd")
                           .price(List.of(Price.create(BigDecimal.TEN, LocalDateTime.now()))).build();
        ProductEntity saved = productRepository.save(ProductEntity.fromModel(p));
        PriceEntity pe = new PriceEntity();
        pe.setPrice(BigDecimal.ONE);
        pe.setTimestamp(LocalDateTime.now().plus(1, ChronoUnit.HOURS));
        priceRepository.saveAll(
                List.of(Tuple.of(saved.getId(), pe))
        );
        return ResponseEntity.ok(List.of("HI"));
    }

    @GetMapping("/save/p")
    public String saveProduct(@RequestParam(value = "price") String price) {
        Product p = Product.builder()
                               .url("url")
                               .providerId("providerId")
                               .name("name")
                               .price(List.of(Price.create(BigDecimal.valueOf(Long.parseLong(price)), LocalDateTime.now()))).build();
        productService.storeAll(HashSet.of(p));
        return "HI";
    }

    @GetMapping("/reindex")
    public void forceReindex() {
        Executors.newSingleThreadExecutor().submit(engine::index);
    }
}

package com.xkom.api;

import com.xkom.engine.IndexingEngine;
import com.xkom.entity.ProductEntity;
import com.xkom.model.Price;
import com.xkom.model.Product;
import com.xkom.repository.ProductRepository;
import io.vavr.collection.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;

//TODO might switch to 'admin api' when security enabled
@RestController
@RequestMapping("/debug-api/")
public class DebugApi {

    private final IndexingEngine engine;
    private final ProductRepository repository;

    public DebugApi(IndexingEngine engine, ProductRepository repository) {
        this.engine = engine;
        this.repository = repository;
    }

    @GetMapping("/test")
    public ResponseEntity<List<String>> test() {
        Product p = Product.builder()
                           .url("asdas")
                           .providerId("asdasd")
                           .name("asdasd")
                           .price(List.of(Price.create(BigDecimal.TEN, LocalDateTime.now()))).build();
        repository.save(ProductEntity.fromModel(p));
        return ResponseEntity.ok(List.of("HI"));
    }

    @GetMapping("/reindex")
    public void forceReindex() {
        Executors.newSingleThreadExecutor().submit(engine::index);
    }
}

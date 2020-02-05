package com.xkom.service;

import com.xkom.engine.Failures;
import com.xkom.entity.PriceEntity;
import com.xkom.entity.ProductEntity;
import com.xkom.model.Product;
import com.xkom.repository.PriceRepository;
import com.xkom.repository.ProductRepository;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class ProductService {

    private final ProductRepository productRepository;
    private final PriceRepository priceRepository;
    private final Logger logger;

    //Cache to avoid querying db for each element to check if already exists
    //key - providerId, value - dbId
    private final Map<String, Long> nameDbIdCache = Collections.synchronizedMap(new HashMap<>());

    public ProductService(ProductRepository productRepository, PriceRepository priceRepository, Logger logger) {
        this.productRepository = productRepository;
        this.priceRepository = priceRepository;
        this.logger = logger;
    }

    public List<Product> getAllLike(String pattern) {
        return Try.of(() -> productRepository.findByNameContaining(pattern))
                  .toList()
                  .flatMap(List::ofAll)
                  .map(Product::fromEntity);
    }

    public void storeAll(Set<Product> products) {
        List<Tuple2<Long, PriceEntity>> alreadyExistEntries = products.filter(product -> nameDbIdCache.containsKey(product.getProviderId()))
                                                                      .toList()
                                                                      .map(this::toEntry);
        Try.run(() -> priceRepository.saveAll(alreadyExistEntries))
           .onSuccess(it -> logger.info("Successfully stored EXISTING products - {}", alreadyExistEntries.length()))
           .onFailure(Failures.unableToStorePrice(logger));

        List<ProductEntity> newOnes = products.filter(product -> !nameDbIdCache.containsKey(product.getProviderId()))
                                              .toList()
                                              .map(ProductEntity::fromModel);

        Try.of(() -> productRepository.saveAll(newOnes))
           .onFailure(Failures.unableToStoreProducts(logger))
           .onSuccess(it -> logger.info("Successfully stored NEW products - {}", newOnes.length()))
           .toList()
           .flatMap(List::ofAll)
           .forEach(entity -> nameDbIdCache.put(entity.getProviderId(), entity.getId()));

        System.out.println(); //FIXME REMOVE
    }

    private Tuple2<Long, PriceEntity> toEntry(Product product) {
        Long id = nameDbIdCache.get(product.getProviderId());
        return Tuple.of(id, PriceEntity.fromModel(product.getPrice().head()));
    }
}

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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class ProductService {

    private final ProductRepository productRepository;
    private final PriceRepository priceRepository;
    private final Logger logger;

    //Cache to avoid querying db for each element to check if already exists
    //Price is also stored to not save same value twice
    //key - providerId, value - dbId,last price
    public final Map<String, Tuple2<Long, BigDecimal>> nameDbIdCache = Collections.synchronizedMap(new HashMap<>());

    public ProductService(ProductRepository productRepository, PriceRepository priceRepository, Logger logger) {
        this.productRepository = productRepository;
        this.priceRepository = priceRepository;
        this.logger = logger;

        productRepository.findAll().forEach(p -> nameDbIdCache.put(
                p.getProviderId(), Tuple.of(p.getId(), p.getPrices().get(p.getPrices().size() - 1).getPrice())
        ));
    }

    public List<Product> getAllLike(String pattern, int page, int amount) {
        return Try.of(() -> productRepository.findByNameContaining(pattern, PageRequest.of(page, amount)))
                  .toList()
                  .flatMap(List::ofAll)
                  .map(Product::fromEntity);
    }

    public void storeAll(Set<Product> products) {
        List<Tuple2<Long, PriceEntity>> eligibleToUpdate = products.filter(this::isEligibleToUpdate)
                                                                   .toList()
                                                                   .peek(this::updateNewPrice)
                                                                   .map(this::toEntry);
        Try.run(() -> priceRepository.saveAll(eligibleToUpdate))
           .onSuccess(it -> logger.info("Successfully stored new product prices - {}", eligibleToUpdate.length()))
           .onFailure(Failures.unableToStorePrice(logger));

        List<ProductEntity> newOnes = products.filter(product -> !nameDbIdCache.containsKey(product.getProviderId()))
                                              .toList()
                                              .map(ProductEntity::fromModel);

        Try.of(() -> productRepository.saveAll(newOnes))
           .onFailure(Failures.unableToStoreProducts(logger))
           .onSuccess(it -> logger.info("Successfully stored new products - {}", newOnes.length()))
           .toList()
           .flatMap(List::ofAll)
           .forEach(entity -> nameDbIdCache.put(entity.getProviderId(), Tuple.of(entity.getId(),
                                                                                 entity.getPrices().get(0).getPrice())));
    }

    private boolean isEligibleToUpdate(Product product) {
        Tuple2<Long, BigDecimal> idLastPrice = nameDbIdCache.get(product.getProviderId());

        if (idLastPrice != null) {
            BigDecimal lastPrice = idLastPrice._2;
            BigDecimal newOne = product.getPrice().last().getPrice();
            //Equals implementation is kind of bugged
            //67.00.equals(67) returns false
            boolean eligible = lastPrice.compareTo(newOne) != 0;
            if (eligible) {
                logger.info("ELIGIBLE ITEM TO FOUND -> PRODUCT: {} CACHE RECORD: {}", product, idLastPrice);
            }
            return eligible;
        }
        return false;
    }

    private void updateNewPrice(Product product) {
        nameDbIdCache.computeIfPresent(
                product.getProviderId(), (k, t) -> Tuple.of(t._1, product.getPrice().get(0).getPrice())
        );
    }

    private Tuple2<Long, PriceEntity> toEntry(Product product) {
        Long id = nameDbIdCache.get(product.getProviderId())._1;
        return Tuple.of(id, PriceEntity.fromModel(product.getPrice().head()));
    }
}

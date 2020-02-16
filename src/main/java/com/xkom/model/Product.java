package com.xkom.model;

import com.xkom.entity.ProductEntity;
import io.vavr.collection.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@AllArgsConstructor
@Getter
@ToString
@Builder
public class Product {

    private final String providerId;
    private final String url;
    private final String name;
    private final List<Price> price;

    public static Product fromEntity(ProductEntity entity) {
        return Product.builder()
                      .name(entity.getName())
                      .providerId(entity.getProviderId())
                      .url(entity.getUrl())
                      .price(List.ofAll(entity.getPrices()).map(Price::fromEntity))
                      .build();
    }

    //In current implementation two products are equal when their provider ids are equal
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(providerId, product.providerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(providerId);
    }
}

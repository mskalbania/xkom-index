package com.xkom.entity;

import com.xkom.model.Product;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Entity
@Table(name = "PRODUCT")
@Data
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "url")
    private String url;

    @OneToMany(cascade = {ALL}, fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    @OrderBy("timestamp ASC")
    private List<PriceEntity> prices = new ArrayList<>();

    public static ProductEntity fromModel(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.setName(product.getName());
        entity.setProviderId(product.getProviderId());
        entity.setUrl(product.getUrl());
        entity.setPrices(product.getPrice().map(PriceEntity::fromModel).asJava());
        return entity;
    }
}

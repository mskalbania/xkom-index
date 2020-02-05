package com.xkom.repository;

import com.xkom.entity.ProductEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProductRepository extends PagingAndSortingRepository<ProductEntity, Long> {

    List<ProductEntity> findByNameContaining(String pattern);
}

package com.xkom.repository;

import com.xkom.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProductRepository extends PagingAndSortingRepository<ProductEntity, Long> {

    Page<ProductEntity> findByNameContaining(String pattern, Pageable pageable);
}

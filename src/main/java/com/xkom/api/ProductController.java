package com.xkom.api;

import com.xkom.model.Product;
import com.xkom.service.ProductService;
import io.vavr.collection.List;
import io.vavr.control.Option;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products/")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("all") //TODO MOVE TO API MODEL
    ResponseEntity<List<Product>> getAllProducts(@RequestParam(value = "like", required = false) String like) {
        String pattern = Option.of(like).getOrElse("");
        return ResponseEntity.ok(productService.getAllLike(pattern));
    }
}

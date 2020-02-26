package com.xkom.api;

import com.xkom.service.ProductService;
import io.vavr.control.Option;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products/")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("all")
    ResponseEntity<?> getAllProducts(@RequestParam(value = "like", required = false) String like,
                                     @RequestParam(value = "page", defaultValue = "1") int page,
                                     @RequestParam(value = "amount", defaultValue = "40") int amount) {
        if(page - 1 < 0 || amount < 0) {
          return ResponseEntity.badRequest().body("Page size and amount need to be > 0");
        }
        String pattern = Option.of(like).getOrElse("");
        return ResponseEntity.ok(productService.getAllLike(pattern, page -1, amount));
    }
}

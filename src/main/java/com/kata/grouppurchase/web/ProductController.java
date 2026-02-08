package com.kata.grouppurchase.web;

import com.fasterxml.jackson.annotation.JsonView;
import com.kata.grouppurchase.dto.ProductDto;
import com.kata.grouppurchase.dto.ProductWithPriceTiersDto;
import com.kata.grouppurchase.dto.Views;
import com.kata.grouppurchase.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public record ProductController(
    ProductService productService
) {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @GetMapping
    @JsonView(Views.Response.class)
    public ResponseEntity<List<ProductDto>> listProducts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Listing products - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Slice<ProductDto> products = productService.getAllProducts(pageable);
        log.info("Returning {} products", products.getNumberOfElements());

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Has-Next", String.valueOf(products.hasNext()));
        headers.add("X-Is-First", String.valueOf(products.isFirst()));
        headers.add("X-Page-Number", String.valueOf(products.getNumber()));
        headers.add("X-Page-Size", String.valueOf(products.getSize()));

        return ResponseEntity.ok().headers(headers).body(products.getContent());
    }

    @GetMapping("/{id}")
    @JsonView(Views.Response.class)
    public ResponseEntity<ProductWithPriceTiersDto> getProductWithPriceTiers(
        @PathVariable UUID id
    ) {
        log.info("Fetching product {} with price tiers", id);

        ProductWithPriceTiersDto product = productService.getProductWithPriceTiers(id);

        return ResponseEntity.ok(product);
    }
}

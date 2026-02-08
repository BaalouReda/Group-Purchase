package com.kata.grouppurchase.service;

import com.kata.grouppurchase.dto.ProductDto;
import com.kata.grouppurchase.dto.ProductWithPriceTiersDto;
import com.kata.grouppurchase.web.exception.ResourceNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.UUID;

/**
 * Service interface for managing products.
 */
public interface ProductService {

    /**
     * Retrieves a paginated list of all active products.
     *
     * @param pageable pagination information (page number, size, sort)
     * @return slice of products
     */
    Slice<ProductDto> getAllProducts(Pageable pageable);

    /**
     * Retrieves a specific product with its associated price tiers.
     *
     * @param productId the UUID of the product
     * @return product with price tiers sorted by threshold ascending
     * @throws ResourceNotFoundException if product not found
     */
    ProductWithPriceTiersDto getProductWithPriceTiers(UUID productId);
}

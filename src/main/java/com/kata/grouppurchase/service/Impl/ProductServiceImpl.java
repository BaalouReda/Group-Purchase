package com.kata.grouppurchase.service.Impl;

import com.kata.grouppurchase.dao.PriceTierEntity;
import com.kata.grouppurchase.dao.ProductEntity;
import com.kata.grouppurchase.dto.PriceTierDto;
import com.kata.grouppurchase.dto.ProductDto;
import com.kata.grouppurchase.dto.ProductWithPriceTiersDto;
import com.kata.grouppurchase.mapper.ProductMapper;
import com.kata.grouppurchase.repository.PriceTierEntityRepository;
import com.kata.grouppurchase.repository.ProductEntityRepository;
import com.kata.grouppurchase.service.ProductService;
import com.kata.grouppurchase.web.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductEntityRepository productRepository;
    private final PriceTierEntityRepository priceTierRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(
        ProductEntityRepository productRepository,
        PriceTierEntityRepository priceTierRepository,
        ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.priceTierRepository = priceTierRepository;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<ProductDto> getAllProducts(Pageable pageable) {
        log.debug("Fetching products with pagination: {}", pageable);

        Slice<ProductEntity> productEntitySlice = productRepository.findAllBy(pageable);

        log.info("Retrieved {} products (page {} of {})",
            productEntitySlice.getNumberOfElements(),
            productEntitySlice.getNumber() + 1,
            productEntitySlice.getSize());

        return productEntitySlice.map(productMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductWithPriceTiersDto getProductWithPriceTiers(UUID productId) {
        log.debug("Fetching product {} with price tiers", productId);

        ProductEntity product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", productId.toString()));

        List<PriceTierEntity> priceTiers = priceTierRepository.findByProductOrderByThresholdAsc(product);

        List<PriceTierDto> priceTierDtos = priceTiers.stream()
            .map(tier -> new PriceTierDto(
                tier.getId(),
                tier.getProduct().getId(),
                null,
                tier.getThreshold(),
                tier.getDiscountPct()
            ))
            .toList();

        log.info("Retrieved product {} with {} price tiers", productId, priceTierDtos.size());

        return new ProductWithPriceTiersDto(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getBasePrice(),
            product.getActive(),
            priceTierDtos
        );
    }
}

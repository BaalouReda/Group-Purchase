package com.kata.grouppurchase.service.Impl;

import com.kata.grouppurchase.dao.PriceTierEntity;
import com.kata.grouppurchase.dao.ProductEntity;
import com.kata.grouppurchase.repository.PriceTierEntityRepository;
import com.kata.grouppurchase.service.PriceCalculationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

@Service
public class PriceCalculationServiceImpl implements PriceCalculationService {

    private static final Logger log = LoggerFactory.getLogger(PriceCalculationServiceImpl.class);
    public static final String HUNDRED = "100";

    private final PriceTierEntityRepository priceTierRepository;

    public PriceCalculationServiceImpl(PriceTierEntityRepository priceTierRepository) {
        this.priceTierRepository = priceTierRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateCurrentPrice(ProductEntity product,Integer currentCount) {
        BigDecimal basePrice = product.getBasePrice();

        if (currentCount <= 1) {
            log.debug("Current count is {} for product {}, returning base price", currentCount, product.getId());
            return basePrice;
        }

        BigDecimal discount =  calculateDiscountPercentage(product, currentCount);
        if(Objects.equals(discount, BigDecimal.ZERO)) {
            log.debug("No applicable tier for product {} with count {}", product.getId(), currentCount);
            return basePrice;
        }

        BigDecimal multiplier = BigDecimal.ONE.subtract(
                discount.divide(new BigDecimal(HUNDRED), 4, RoundingMode.HALF_UP)
        );
        BigDecimal finalPrice = basePrice.multiply(multiplier)
                .setScale(2, RoundingMode.HALF_UP);

        log.debug("Price for product {} with count {}: base={}, discount={}%, final={}",
                product.getId(), currentCount, basePrice, discount, finalPrice);

        return finalPrice;
    }

    @Override
    public Optional<PriceTierEntity> findApplicableTier(ProductEntity product,Integer currentCount) {
        log.debug("Find applicable tier for product {} with count {}", product.getId(), currentCount);
        return priceTierRepository.findApplicableTierForProduct(product.getId(), currentCount);
    }

    @Override
    public BigDecimal calculateDiscountPercentage(ProductEntity product,Integer currentCount) {
        log.debug("Calculate discount percentage for product {} with count {}", product.getId(), currentCount);
        return findApplicableTier(product, currentCount)
                .map(PriceTierEntity::getDiscountPct)
                .orElse(BigDecimal.ZERO);
    }
}

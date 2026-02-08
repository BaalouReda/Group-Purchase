package com.kata.grouppurchase.service;

import com.kata.grouppurchase.dao.PriceTierEntity;
import com.kata.grouppurchase.dao.ProductEntity;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Service interface for calculating prices based on volume discounts.
 */
public interface PriceCalculationService {

    /**
     * Calculates current price for a product based on participant count and applicable tier.
     * Finds the highest tier threshold that is less than or equal to the current count
     * and applies the corresponding discount to the base price.
     *
     * If no tier applies (count below minimum threshold or no tiers exist), returns the base price.
     *
     * @param product the product entity containing base price
     * @param currentCount the current number of participants in the group purchase
     * @return the calculated price after applying tier discount, or base price if no tier applies
     */
    BigDecimal calculateCurrentPrice(ProductEntity product, Integer currentCount);

    /**
     * Finds the applicable price tier for the given participant count.
     * Returns the tier with the highest threshold that is less than or equal to the current count.
     *
     * This follows the logic: for tiers sorted by threshold ascending, find the last tier
     * where threshold <= currentCount.
     *
     * @param product the product entity
     * @param currentCount the current number of participants
     * @return optional containing the applicable tier, empty if no tier applies
     */
    Optional<PriceTierEntity> findApplicableTier(ProductEntity product, Integer currentCount);

    /**
     * Calculates the discount percentage applicable for the given participant count.
     * Returns 0.00 if no tier threshold is met.
     *
     * @param product the product entity
     * @param currentCount the current number of participants
     * @return discount percentage (0.00 to 100.00), or 0.00 if no tier applies
     */
    BigDecimal calculateDiscountPercentage(ProductEntity product, Integer currentCount);
}

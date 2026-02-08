package com.kata.grouppurchase.repository;

import com.kata.grouppurchase.dao.PriceTierEntity;
import com.kata.grouppurchase.dao.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PriceTierEntityRepository extends JpaRepository<PriceTierEntity, UUID> {
    List<PriceTierEntity> findByProductOrderByThresholdAsc(ProductEntity product);

    @Query(value = """
        SELECT * FROM t_price_tier
        WHERE product_id = :productId
        AND threshold <= :currentCount
        ORDER BY threshold DESC
        LIMIT 1
        """, nativeQuery = true)
    Optional<PriceTierEntity> findApplicableTierForProduct(@Param("productId") UUID id,
                                                           @Param("currentCount") Integer currentCount);
}
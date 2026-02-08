package com.kata.grouppurchase.repository;

import com.kata.grouppurchase.dao.ProductEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductEntityRepository extends JpaRepository<ProductEntity, UUID> {
    Slice<ProductEntity> findAllBy(Pageable pageable);
}
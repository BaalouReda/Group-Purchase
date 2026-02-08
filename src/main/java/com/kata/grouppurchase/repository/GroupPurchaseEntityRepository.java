package com.kata.grouppurchase.repository;

import com.kata.grouppurchase.dao.GroupPurchaseEntity;
import com.kata.grouppurchase.enums.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupPurchaseEntityRepository extends JpaRepository<GroupPurchaseEntity, UUID>, JpaSpecificationExecutor<GroupPurchaseEntity> {
    Optional<GroupPurchaseEntity> findById(Long groupPurchaseId);

    Slice<GroupPurchaseEntity> findByStatus(Status status, Pageable pageable);

    Slice<GroupPurchaseEntity> findAllBy(Pageable pageable);
}

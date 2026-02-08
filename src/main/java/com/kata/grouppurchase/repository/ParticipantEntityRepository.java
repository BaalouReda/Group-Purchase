package com.kata.grouppurchase.repository;

import com.kata.grouppurchase.dao.CustomerEntity;
import com.kata.grouppurchase.dao.GroupPurchaseEntity;
import com.kata.grouppurchase.dao.ParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ParticipantEntityRepository extends JpaRepository<ParticipantEntity, UUID> {

    boolean existsParticipantEntitiesByGroupAndCustomer(GroupPurchaseEntity groupPurchase, CustomerEntity customer);
}
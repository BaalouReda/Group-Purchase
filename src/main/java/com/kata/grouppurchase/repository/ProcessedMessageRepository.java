package com.kata.grouppurchase.repository;

import com.kata.grouppurchase.dao.ProcessedMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessageEntity, String> {
}

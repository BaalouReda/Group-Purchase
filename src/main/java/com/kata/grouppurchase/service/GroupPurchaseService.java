package com.kata.grouppurchase.service;

import com.kata.grouppurchase.dto.GroupPurchaseDto;
import com.kata.grouppurchase.enums.Status;
import com.kata.grouppurchase.web.exception.GroupPurchaseException;
import com.kata.grouppurchase.web.exception.ResourceNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.UUID;

/**
 * Service interface for managing group purchases.
 */
public interface GroupPurchaseService {

    /**
     * Creates a new group purchase.
     * The creator is automatically added as the first participant with current count set to 1.
     * A Spring event is published to schedule deadline processing via RabbitMQ.
     *
     * @param groupPurchaseDto the group purchase data containing product, participants range, and deadline
     * @return the created group purchase with generated ID and initial status PENDING
     * @throws ResourceNotFoundException if product not found
     * @throws GroupPurchaseException if business rules are violated (invalid participant range, inactive product)
     */
    GroupPurchaseDto createGroupPurchase(GroupPurchaseDto groupPurchaseDto);

    /**
     * Allows a customer to join an existing group purchase.
     * Validates business rules (status, deadline, available spots, not already joined).
     * Automatically transitions status to FULL when max participants is reached.
     *
     * @param groupPurchaseId the UUID of the group purchase to join
     * @return the updated group purchase with incremented participant count
     * @throws ResourceNotFoundException if group purchase not found
     * @throws GroupPurchaseException if business rules are violated:
     *         - Already joined
     *         - Status is not PENDING
     *         - Deadline has passed
     *         - Group is full (max participants reached)
     */
    void joinGroupPurchase(UUID groupPurchaseId);

    /**
     * Retrieves a specific group purchase by ID.
     *
     * @param groupPurchaseId the UUID of the group purchase
     * @return the group purchase details
     * @throws ResourceNotFoundException if group purchase not found
     */
    GroupPurchaseDto getGroupPurchaseById(UUID groupPurchaseId);

    /**
     * Retrieves a paginated list of group purchases, optionally filtered by status.
     *
     * @param status optional status filter (PENDING, FULL, FINALIZED, CANCELLED)
     * @param pageable pagination information (page number, size, sort)
     * @return slice of group purchases
     */
    Slice<GroupPurchaseDto> getAllGroupPurchases(Status status, Pageable pageable);
}

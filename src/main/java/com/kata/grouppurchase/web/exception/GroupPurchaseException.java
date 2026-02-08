package com.kata.grouppurchase.web.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for group purchase business rule violations.
 * Provides factory methods for common group purchase error scenarios.
 */
public class GroupPurchaseException extends BusinessException {

    private GroupPurchaseException(String messageCode, HttpStatus httpStatus) {
        super(messageCode, httpStatus);
    }

    private GroupPurchaseException(String messageCode, HttpStatus httpStatus, String[] messageArgs) {
        super(messageCode, httpStatus, messageArgs);
    }

    /**
     * Thrown when attempting to create a group purchase for an inactive product.
     */
    public static GroupPurchaseException productNotActive() {
        return new GroupPurchaseException(
            "error.group.purchase.product.not.active",
            HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Thrown when minimum participants is greater than or equal to maximum participants.
     * Note: This is now primarily handled by DTO validation.
     */
    public static GroupPurchaseException invalidParticipantRange() {
        return new GroupPurchaseException(
            "error.group.purchase.invalid.participant.range",
            HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Thrown when a customer attempts to join a group purchase they've already joined.
     */
    public static GroupPurchaseException alreadyJoined() {
        return new GroupPurchaseException(
            "error.group.purchase.already.joined",
            HttpStatus.CONFLICT
        );
    }

    /**
     * Thrown when attempting to join a group purchase that has reached maximum capacity.
     */
    public static GroupPurchaseException groupFull() {
        return new GroupPurchaseException(
            "error.group.purchase.full",
            HttpStatus.CONFLICT
        );
    }

    /**
     * Thrown when attempting to join a group purchase after its deadline has passed.
     */
    public static GroupPurchaseException deadlinePassed() {
        return new GroupPurchaseException(
            "error.group.purchase.deadline.passed",
            HttpStatus.GONE
        );
    }

    /**
     * Thrown when attempting an operation on a group purchase with an invalid status.
     *
     * @param status the current status of the group purchase
     */
    public static GroupPurchaseException invalidStatus(String status) {
        return new GroupPurchaseException(
            "error.group.purchase.invalid.status",
            HttpStatus.CONFLICT,
            new String[]{status}
        );
    }
}

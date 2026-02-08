package com.kata.grouppurchase.event.dto;

import com.kata.grouppurchase.enums.EventType;

import java.util.UUID;

public record GroupPurchaseCreatedEvent(
        UUID groupPurchaseId,
        EventType eventType
) {
}

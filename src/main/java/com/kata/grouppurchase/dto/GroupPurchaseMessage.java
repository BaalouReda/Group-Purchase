package com.kata.grouppurchase.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GroupPurchaseMessage(@NotNull UUID purchaseId) {
}

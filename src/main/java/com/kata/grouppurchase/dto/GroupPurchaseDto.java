package com.kata.grouppurchase.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import com.kata.grouppurchase.enums.Status;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

public record GroupPurchaseDto(
    @JsonView(Views.Response.class)
    UUID id,

    @JsonView(Views.Request.class)
    @NotNull(message = "{validation.group.purchase.product.id.required}")
    UUID productId,

    @JsonView(Views.Response.class)
    ProductDto product,

    @JsonView(Views.Response.class)
    CustomerDto creator,

    @JsonView({Views.Request.class, Views.Response.class})
    @NotNull(message = "{validation.group.purchase.min.participants.required}")
    @Min(value = 2, message = "{validation.group.purchase.min.participants.min}")
    Integer minParticipants,

    @JsonView({Views.Request.class, Views.Response.class})
    @NotNull(message = "{validation.group.purchase.max.participants.required}")
    @Min(value = 2, message = "{validation.group.purchase.max.participants.min}")
    Integer maxParticipants,

    @JsonView(Views.Response.class)
    Integer currentCount,

    @JsonView(Views.Response.class)
    Status status,

    @JsonView({Views.Request.class, Views.Response.class})
    @NotNull(message = "{validation.group.purchase.deadline.required}")
    OffsetDateTime deadline,

    @JsonView(Views.Response.class)
    BigDecimal currentPrice
) {
    @AssertTrue(message = "{validation.group.purchase.participant.range}")
    @JsonIgnore
    public boolean isValidParticipantRange() {
        if (minParticipants == null || maxParticipants == null) {
            return true;
        }
        return minParticipants <= maxParticipants;
    }

    @AssertTrue(message = "{validation.group.purchase.deadline.future}")
    @JsonIgnore
    public boolean isDeadlineValid() {
        if (deadline == null) {
            return true;
        }
        return deadline.isAfter(OffsetDateTime.now(ZoneId.systemDefault())
                                              .withOffsetSameLocal(deadline.getOffset()));
    }
}

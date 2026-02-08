package com.kata.grouppurchase.dto;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ParticipantDto(
    @JsonView(Views.Response.class)
    UUID id,

    @JsonView({Views.Request.class, Views.Response.class})
    @NotNull(message = "{validation.participant.group.id.required}")
    UUID groupId,

    @JsonView(Views.Request.class)
    @NotNull(message = "{validation.participant.customer.id.required}")
    UUID customerId,

    @JsonView(Views.Response.class)
    CustomerDto customer
) {
}

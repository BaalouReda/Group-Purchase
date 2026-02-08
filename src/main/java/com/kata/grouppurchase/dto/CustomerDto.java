package com.kata.grouppurchase.dto;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CustomerDto(
    @JsonView(Views.Response.class)
    UUID id,

    @JsonView({Views.Request.class, Views.Response.class})
    @NotBlank(message = "{validation.customer.first.name.required}")
    String firstName,

    @JsonView({Views.Request.class, Views.Response.class})
    @NotBlank(message = "{validation.customer.last.name.required}")
    String lastName,

    @JsonView({Views.Request.class, Views.Response.class})
    String phone
) {
}

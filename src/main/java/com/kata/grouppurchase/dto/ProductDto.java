package com.kata.grouppurchase.dto;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductDto(
    @JsonView(Views.Response.class)
    UUID id,

    @JsonView({Views.Request.class, Views.Response.class})
    @NotBlank(message = "{validation.product.name.required}")
    String name,

    @JsonView({Views.Request.class, Views.Response.class})
    String description,

    @JsonView({Views.Request.class, Views.Response.class})
    @NotNull(message = "{validation.product.base.price.required}")
    @DecimalMin(value = "0.01", message = "{validation.product.base.price.min}")
    BigDecimal basePrice,

    @JsonView({Views.Request.class, Views.Response.class})
    Boolean active
) {
}

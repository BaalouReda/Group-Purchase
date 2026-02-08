package com.kata.grouppurchase.dto;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record PriceTierDto(
    @JsonView(Views.Response.class)
    UUID id,

    @JsonView(Views.Request.class)
    @NotNull(message = "{validation.price.tier.product.id.required}")
    UUID productId,

    @JsonView(Views.Response.class)
    ProductDto product,

    @JsonView({Views.Request.class, Views.Response.class})
    @NotNull(message = "{validation.price.tier.threshold.required}")
    @Min(value = 1, message = "{validation.price.tier.threshold.min}")
    Integer threshold,

    @JsonView({Views.Request.class, Views.Response.class})
    @NotNull(message = "{validation.price.tier.discount.pct.required}")
    @DecimalMin(value = "0.01", message = "{validation.price.tier.discount.pct.min}")
    @DecimalMax(value = "100.00", message = "{validation.price.tier.discount.pct.max}")
    BigDecimal discountPct
) {
}

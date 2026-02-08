package com.kata.grouppurchase.dto;

import com.fasterxml.jackson.annotation.JsonView;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductWithPriceTiersDto(
    @JsonView(Views.Response.class)
    UUID id,

    @JsonView(Views.Response.class)
    String name,

    @JsonView(Views.Response.class)
    String description,

    @JsonView(Views.Response.class)
    BigDecimal basePrice,

    @JsonView(Views.Response.class)
    Boolean active,

    @JsonView(Views.Response.class)
    List<PriceTierDto> priceTiers
) {
}

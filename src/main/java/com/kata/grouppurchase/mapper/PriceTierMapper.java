package com.kata.grouppurchase.mapper;

import com.kata.grouppurchase.dao.PriceTierEntity;
import com.kata.grouppurchase.dto.PriceTierDto;
import com.kata.grouppurchase.dto.ProductDto;
import org.springframework.stereotype.Component;

@Component
public class PriceTierMapper implements BaseMapper<PriceTierDto, PriceTierEntity> {

    private final ProductMapper productMapper;

    public PriceTierMapper(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public PriceTierDto toDto(PriceTierEntity entity) {
        if (entity == null) {
            return null;
        }

        ProductDto productDto = productMapper.toDto(entity.getProduct());

        return new PriceTierDto(
            entity.getId(),
            entity.getProduct() != null ? entity.getProduct().getId() : null,
            productDto,
            entity.getThreshold(),
            entity.getDiscountPct()
        );
    }

    @Override
    public PriceTierEntity toEntity(PriceTierDto dto) {
        if (dto == null) {
            return null;
        }

        PriceTierEntity entity = new PriceTierEntity();
        if (dto.id() != null) {
            entity.setId(dto.id());
        }
        entity.setThreshold(dto.threshold());
        entity.setDiscountPct(dto.discountPct());

        return entity;
    }
}

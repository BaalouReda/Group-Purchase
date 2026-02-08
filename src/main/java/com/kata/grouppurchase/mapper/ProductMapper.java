package com.kata.grouppurchase.mapper;

import com.kata.grouppurchase.dao.ProductEntity;
import com.kata.grouppurchase.dto.ProductDto;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper implements BaseMapper<ProductDto, ProductEntity> {

    @Override
    public ProductDto toDto(ProductEntity entity) {
        if (entity == null) {
            return null;
        }

        return new ProductDto(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getBasePrice(),
            entity.getActive()
        );
    }

    @Override
    public ProductEntity toEntity(ProductDto dto) {
        if (dto == null) {
            return null;
        }

        ProductEntity entity = new ProductEntity();
        if (dto.id() != null) {
            entity.setId(dto.id());
        }
        entity.setName(dto.name());
        entity.setDescription(dto.description());
        entity.setBasePrice(dto.basePrice());
        entity.setActive(dto.active());

        return entity;
    }
}

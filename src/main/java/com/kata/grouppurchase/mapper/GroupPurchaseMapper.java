package com.kata.grouppurchase.mapper;

import com.kata.grouppurchase.dao.GroupPurchaseEntity;
import com.kata.grouppurchase.dto.CustomerDto;
import com.kata.grouppurchase.dto.GroupPurchaseDto;
import com.kata.grouppurchase.dto.ProductDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GroupPurchaseMapper implements BaseMapper<GroupPurchaseDto, GroupPurchaseEntity> {

    private final ProductMapper productMapper;
    private final CustomerMapper customerMapper;

    public GroupPurchaseMapper(ProductMapper productMapper, CustomerMapper customerMapper) {
        this.productMapper = productMapper;
        this.customerMapper = customerMapper;
    }

    @Override
    public GroupPurchaseDto toDto(GroupPurchaseEntity entity) {
        if (entity == null) {
            return null;
        }

        ProductDto productDto = productMapper.toDto(entity.getProduct());
        CustomerDto creatorDto = customerMapper.toDto(entity.getCreator());

        return new GroupPurchaseDto(
            entity.getId(),
            entity.getProduct().getId(),
            productDto,
            creatorDto,
            entity.getMinParticipants(),
            entity.getMaxParticipants(),
            entity.getCurrentCount(),
            entity.getStatus(),
            entity.getDeadline(),
            entity.getCurrentPrice()
        );
    }

    @Override
    public GroupPurchaseEntity toEntity(GroupPurchaseDto dto) {
        if (dto == null) {
            return null;
        }

        GroupPurchaseEntity entity = new GroupPurchaseEntity();
        if (dto.id() != null) {
            entity.setId(dto.id());
        } else {
            entity.setId(UUID.randomUUID());
        }
        entity.setMinParticipants(dto.minParticipants());
        entity.setMaxParticipants(dto.maxParticipants());
        entity.setDeadline(dto.deadline());

        return entity;
    }
}

package com.kata.grouppurchase.mapper;

import com.kata.grouppurchase.dao.ParticipantEntity;
import com.kata.grouppurchase.dto.CustomerDto;
import com.kata.grouppurchase.dto.ParticipantDto;
import org.springframework.stereotype.Component;

@Component
public class ParticipantMapper implements BaseMapper<ParticipantDto, ParticipantEntity> {

    private final CustomerMapper customerMapper;

    public ParticipantMapper(CustomerMapper customerMapper) {
        this.customerMapper = customerMapper;
    }

    @Override
    public ParticipantDto toDto(ParticipantEntity entity) {
        if (entity == null) {
            return null;
        }

        CustomerDto customerDto = customerMapper.toDto(entity.getCustomer());

        return new ParticipantDto(
            entity.getId(),
            entity.getGroup() != null ? entity.getGroup().getId() : null,
            entity.getCustomer() != null ? entity.getCustomer().getId() : null,
            customerDto
        );
    }

    @Override
    public ParticipantEntity toEntity(ParticipantDto dto) {
        if (dto == null) {
            return null;
        }

        ParticipantEntity entity = new ParticipantEntity();
        if (dto.id() != null) {
            entity.setId(dto.id());
        }

        return entity;
    }
}


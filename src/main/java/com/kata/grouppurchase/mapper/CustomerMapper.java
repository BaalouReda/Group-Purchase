package com.kata.grouppurchase.mapper;

import com.kata.grouppurchase.dao.CustomerEntity;
import com.kata.grouppurchase.dto.CustomerDto;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper implements BaseMapper<CustomerDto, CustomerEntity> {

    @Override
    public CustomerDto toDto(CustomerEntity entity) {
        if (entity == null) {
            return null;
        }

        return new CustomerDto(
            entity.getId(),
            entity.getFirstName(),
            entity.getLastName(),
            entity.getPhone()
        );
    }

    @Override
    public CustomerEntity toEntity(CustomerDto dto) {
        if (dto == null) {
            return null;
        }

        CustomerEntity entity = new CustomerEntity();
        if (dto.id() != null) {
            entity.setId(dto.id());
        }
        entity.setFirstName(dto.firstName());
        entity.setLastName(dto.lastName());
        entity.setPhone(dto.phone());

        return entity;
    }
}

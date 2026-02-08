package com.kata.grouppurchase.helper;

import com.kata.grouppurchase.dao.CustomerEntity;
import com.kata.grouppurchase.dao.UserEntity;
import com.kata.grouppurchase.web.exception.ResourceNotFoundException;
import com.kata.grouppurchase.repository.CustomerEntityRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationHelper {
    private final CustomerEntityRepository customerRepository;

    public AuthenticationHelper(CustomerEntityRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("User", "current");
        }
        return (UserEntity) authentication.getPrincipal();
    }

    public CustomerEntity getCurrentCustomer() {
        UserEntity user = getCurrentUser();
        return customerRepository.findByUserId(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Customer", user.getId().toString()));
    }
}

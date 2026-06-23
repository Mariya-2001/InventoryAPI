package com.mariya.inventory.customer.service;

import com.mariya.inventory.customer.dto.CreateCustomerRequest;
import com.mariya.inventory.customer.dto.CustomerResponse;
import com.mariya.inventory.customer.entity.Customer;
import com.mariya.inventory.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (customerRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Customer email already exists");
        }

        Customer customer = Customer.builder()
                .name(request.getName().trim())
                .email(email)
                .build();

        return CustomerResponse.from(customerRepository.save(customer));
    }
}
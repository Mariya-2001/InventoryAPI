package com.mariya.inventory.customer.dto;

import com.mariya.inventory.customer.entity.Customer;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomerResponse {

    private Long id;
    private String name;
    private String email;

    public static CustomerResponse from(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .build();
    }
}
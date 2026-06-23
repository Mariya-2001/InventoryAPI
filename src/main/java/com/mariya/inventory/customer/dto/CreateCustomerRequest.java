package com.mariya.inventory.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCustomerRequest {

    @NotBlank(message = "Customer name is required")
    private String name;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Customer email must be valid")
    private String email;
}
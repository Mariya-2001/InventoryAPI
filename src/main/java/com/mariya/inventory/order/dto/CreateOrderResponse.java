package com.mariya.inventory.order.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateOrderResponse {
    private Long id;
    private Long customerId;
    private String status;
}
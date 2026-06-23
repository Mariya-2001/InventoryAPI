package com.mariya.inventory.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockAdjustmentRequest {

    @NotNull(message = "Quantity change is required")
    private Integer quantityChange;
}
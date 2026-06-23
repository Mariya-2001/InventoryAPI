package com.mariya.inventory.order.controller;

import com.mariya.inventory.order.dto.*;
import com.mariya.inventory.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/api/customers/{customerId}/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateOrderResponse createDraftOrder(@PathVariable Long customerId) {
        return orderService.createDraftOrder(customerId);
    }

    @PostMapping("/api/orders/{orderId}/items")
    public OrderResponse addItem(
            @PathVariable Long orderId,
            @Valid @RequestBody AddOrderItemRequest request
    ) {
        return orderService.addItem(orderId, request);
    }

    @PatchMapping("/api/orders/{orderId}/items/{itemId}")
    public OrderResponse updateItem(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateOrderItemRequest request
    ) {
        return orderService.updateItem(orderId, itemId, request);
    }

    @DeleteMapping("/api/orders/{orderId}/items/{itemId}")
    public OrderResponse removeItem(
            @PathVariable Long orderId,
            @PathVariable Long itemId
    ) {
        return orderService.removeItem(orderId, itemId);
    }

    @GetMapping("/api/orders/{orderId}")
    public OrderResponse getOrder(@PathVariable Long orderId) {
        return orderService.getOrder(orderId);
    }

    @PostMapping("/api/orders/{orderId}/confirm")
    public OrderResponse confirmOrder(@PathVariable Long orderId) {
        return orderService.confirmOrder(orderId);
    }

    @PostMapping("/api/orders/{orderId}/status")
    public OrderResponse updateStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        return orderService.updateStatus(orderId, request);
    }
}
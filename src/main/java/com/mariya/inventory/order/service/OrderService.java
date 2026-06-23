package com.mariya.inventory.order.service;

import com.mariya.inventory.customer.entity.Customer;
import com.mariya.inventory.customer.repository.CustomerRepository;
import com.mariya.inventory.order.dto.*;
import com.mariya.inventory.order.entity.CustomerOrder;
import com.mariya.inventory.order.entity.OrderItem;
import com.mariya.inventory.order.entity.OrderStatus;
import com.mariya.inventory.order.repository.CustomerOrderRepository;
import com.mariya.inventory.order.repository.OrderItemRepository;
import com.mariya.inventory.product.entity.Product;
import com.mariya.inventory.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CustomerOrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public CreateOrderResponse createDraftOrder(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        CustomerOrder order = CustomerOrder.builder()
                .customer(customer)
                .status(OrderStatus.DRAFT)
                .totalAmount(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .build();

        CustomerOrder savedOrder = orderRepository.save(order);

        return CreateOrderResponse.builder()
                .id(savedOrder.getId())
                .customerId(customer.getId())
                .status(savedOrder.getStatus().name())
                .build();
    }

    @Transactional
    public OrderResponse addItem(Long orderId, AddOrderItemRequest request) {
        CustomerOrder order = getOrderEntity(orderId);
        ensureDraft(order);

        Product product = getProductEntity(request.getProductId());

        OrderItem item = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(request.getQuantity())
                .unitPrice(null)
                .lineTotal(null)
                .build();

        order.getItems().add(item);

        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse updateItem(Long orderId, Long itemId, UpdateOrderItemRequest request) {
        CustomerOrder order = getOrderEntity(orderId);
        ensureDraft(order);

        OrderItem item = findOrderItem(order, itemId);
        item.setQuantity(request.getQuantity());

        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse removeItem(Long orderId, Long itemId) {
        CustomerOrder order = getOrderEntity(orderId);
        ensureDraft(order);

        OrderItem item = findOrderItem(order, itemId);
        order.getItems().remove(item);

        return OrderResponse.from(orderRepository.save(order));
    }

    public OrderResponse getOrder(Long orderId) {
        return OrderResponse.from(getOrderEntity(orderId));
    }

    @Transactional
    public OrderResponse confirmOrder(Long orderId) {
        CustomerOrder order = getOrderEntity(orderId);
        ensureDraft(order);

        if (order.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot confirm an empty order");
        }

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException(
                        "Insufficient stock for product: " + product.getName()
                );
            }
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();

            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());

            BigDecimal unitPrice = product.getPrice();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

            item.setUnitPrice(unitPrice);
            item.setLineTotal(lineTotal);

            totalAmount = totalAmount.add(lineTotal);
        }

        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setConfirmedAt(LocalDateTime.now());

        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, UpdateOrderStatusRequest request) {
        CustomerOrder order = getOrderEntity(orderId);
        OrderStatus newStatus = request.getStatus();

        if (newStatus == OrderStatus.SHIPPED) {
            return shipOrder(order);
        }

        if (newStatus == OrderStatus.DELIVERED) {
            return deliverOrder(order);
        }

        if (newStatus == OrderStatus.CANCELLED) {
            return cancelOrder(order);
        }

        throw new IllegalArgumentException("Unsupported status update");
    }

    private OrderResponse shipOrder(CustomerOrder order) {
        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalArgumentException("Only CONFIRMED orders can be shipped");
        }

        order.setStatus(OrderStatus.SHIPPED);
        order.setShippedAt(LocalDateTime.now());

        return OrderResponse.from(orderRepository.save(order));
    }

    private OrderResponse deliverOrder(CustomerOrder order) {
        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new IllegalArgumentException("Only SHIPPED orders can be delivered");
        }

        order.setStatus(OrderStatus.DELIVERED);
        order.setDeliveredAt(LocalDateTime.now());

        return OrderResponse.from(orderRepository.save(order));
    }

    private OrderResponse cancelOrder(CustomerOrder order) {
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Order is already cancelled");
        }

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("Delivered orders cannot be cancelled");
        }

        if (order.getStatus() == OrderStatus.CONFIRMED || order.getStatus() == OrderStatus.SHIPPED) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            }
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());

        return OrderResponse.from(orderRepository.save(order));
    }

    private CustomerOrder getOrderEntity(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    private Product getProductEntity(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    private void ensureDraft(CustomerOrder order) {
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new IllegalArgumentException("Order must be in DRAFT status");
        }
    }

    private OrderItem findOrderItem(CustomerOrder order, Long itemId) {
        return order.getItems()
                .stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Order item not found"));
    }
}
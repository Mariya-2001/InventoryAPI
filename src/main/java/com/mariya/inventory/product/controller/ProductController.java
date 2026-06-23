package com.mariya.inventory.product.controller;

import com.mariya.inventory.product.dto.CreateProductRequest;
import com.mariya.inventory.product.dto.ProductResponse;
import com.mariya.inventory.product.dto.StockAdjustmentRequest;
import com.mariya.inventory.product.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@Valid @RequestBody CreateProductRequest request) {
        return productService.createProduct(request);
    }

    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping("/{id}/stock-adjustments")
    public ProductResponse adjustStock(
            @PathVariable Long id,
            @Valid @RequestBody StockAdjustmentRequest request
    ) {
        return productService.adjustStock(id, request);
    }

    @GetMapping("/low-stock")
    public List<ProductResponse> getLowStockProducts(
            @RequestParam @Min(0) Integer threshold
    ) {
        return productService.getLowStockProducts(threshold);
    }
}
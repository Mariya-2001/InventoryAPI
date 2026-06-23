package com.mariya.inventory.product.service;

import com.mariya.inventory.category.entity.Category;
import com.mariya.inventory.category.repository.CategoryRepository;
import com.mariya.inventory.product.dto.CreateProductRequest;
import com.mariya.inventory.product.dto.ProductResponse;
import com.mariya.inventory.product.dto.StockAdjustmentRequest;
import com.mariya.inventory.product.entity.Product;
import com.mariya.inventory.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductResponse createProduct(CreateProductRequest request) {
        if (productRepository.existsBySkuIgnoreCase(request.getSku().trim())) {
            throw new IllegalArgumentException("Product SKU already exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Product product = Product.builder()
                .name(request.getName().trim())
                .sku(request.getSku().trim())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .category(category)
                .build();

        return ProductResponse.from(productRepository.save(product));
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductResponse::from)
                .toList();
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        return ProductResponse.from(product);
    }

    public ProductResponse adjustStock(Long productId, StockAdjustmentRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        int newQuantity = product.getStockQuantity() + request.getQuantityChange();

        if (newQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot become negative");
        }

        product.setStockQuantity(newQuantity);

        return ProductResponse.from(productRepository.save(product));
    }

    public List<ProductResponse> getLowStockProducts(Integer threshold) {
        return productRepository.findByStockQuantityLessThan(threshold)
                .stream()
                .map(ProductResponse::from)
                .toList();
    }
}
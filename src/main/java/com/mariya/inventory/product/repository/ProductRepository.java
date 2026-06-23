package com.mariya.inventory.product.repository;

import com.mariya.inventory.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsBySkuIgnoreCase(String sku);

    List<Product> findByStockQuantityLessThan(Integer threshold);
}

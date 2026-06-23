package com.mariya.inventory.category.service;

import com.mariya.inventory.category.dto.CategoryResponse;
import com.mariya.inventory.category.dto.CreateCategoryRequest;
import com.mariya.inventory.category.entity.Category;
import com.mariya.inventory.category.repository.CategoryRepository;
import com.mariya.inventory.exception.BusinessConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse createCategory(CreateCategoryRequest request) {
        String name = request.getName().trim();

        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new BusinessConflictException("Category already exists");
        }

        Category category = Category.builder()
                .name(name)
                .build();

        return CategoryResponse.from(categoryRepository.save(category));
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }
}
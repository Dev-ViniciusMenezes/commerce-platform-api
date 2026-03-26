package com.viniciusdev.commerceapi.service;

import com.viniciusdev.commerceapi.database.model.Category;
import com.viniciusdev.commerceapi.dto.CategoryRequest;
import com.viniciusdev.commerceapi.dto.CategoryResponse;
import com.viniciusdev.commerceapi.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;


    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = new Category(null, request.name());
        categoryRepository.save(category);
        return new CategoryResponse(
                category.getId(),
                category.getName()
        );
    }

    public CategoryResponse updateCategory (Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        category.setName(request.name());
        categoryRepository.save(category);
        return new CategoryResponse(
                category.getId(),
                category.getName()
        );
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        return new CategoryResponse(
                category.getId(),
                category.getName()
        );
    }

    public List<CategoryResponse> getAllCategories() {
        List <Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName()
                ))
                .toList();
    }

}


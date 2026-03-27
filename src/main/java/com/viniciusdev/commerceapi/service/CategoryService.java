package com.viniciusdev.commerceapi.service;

import com.viniciusdev.commerceapi.database.model.Category;
import com.viniciusdev.commerceapi.dto.CategoryRequest;
import com.viniciusdev.commerceapi.dto.CategoryResponse;
import com.viniciusdev.commerceapi.mapper.CategoryMapper;
import com.viniciusdev.commerceapi.database.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;


    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = categoryMapper.toEntity(request);
        categoryRepository.save(category);
        return categoryMapper.toDTO(category);
    }

    public CategoryResponse updateCategory (Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        categoryMapper.toUpdate(category, request);
        categoryRepository.save(category);
        return categoryMapper.toDTO(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        return categoryMapper.toDTO(category);
    }

    public List<CategoryResponse> getAllCategories() {
        List <Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(categoryMapper::toDTO)
                .toList();
    }

}


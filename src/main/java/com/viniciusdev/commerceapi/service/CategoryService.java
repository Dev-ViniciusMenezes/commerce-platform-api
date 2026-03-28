package com.viniciusdev.commerceapi.service;

import com.viniciusdev.commerceapi.database.model.Category;
import com.viniciusdev.commerceapi.dto.CategoryRequest;
import com.viniciusdev.commerceapi.dto.CategoryResponse;
import com.viniciusdev.commerceapi.exception.BusinessException;
import com.viniciusdev.commerceapi.exception.ResourceNotFoundException;
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
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found " + id));
        categoryMapper.toUpdate(category, request);
        categoryRepository.save(category);
        return categoryMapper.toDTO(category);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found " + id));
        if (!category.getProducts().isEmpty()) {
            throw new BusinessException("Cannot delete category with products");
        }
        categoryRepository.delete(category);
    }

    public CategoryResponse getCategoryById(Long id)  {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found " + id));
        return categoryMapper.toDTO(category);
    }

    public List<CategoryResponse> getAllCategories() {
        List <Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(categoryMapper::toDTO)
                .toList();
    }

}


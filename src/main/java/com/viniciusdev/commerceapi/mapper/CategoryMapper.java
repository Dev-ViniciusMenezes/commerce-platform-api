package com.viniciusdev.commerceapi.mapper;

import com.viniciusdev.commerceapi.database.model.Category;
import com.viniciusdev.commerceapi.dto.CategoryRequest;
import com.viniciusdev.commerceapi.dto.CategoryResponse;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity (CategoryRequest request) {
        Category category = new Category(null, request.name());
        return category;
    }

    public CategoryResponse toDTO (Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }

    public Category toUpdate (Category category, CategoryRequest request) {
        if (request.name() != null) {
            category.setName(request.name());
        }
        return category;
    }
}

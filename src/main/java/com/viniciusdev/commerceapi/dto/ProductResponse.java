package com.viniciusdev.commerceapi.dto;

import com.viniciusdev.commerceapi.database.model.Category;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Set<Category> categories
) {
}

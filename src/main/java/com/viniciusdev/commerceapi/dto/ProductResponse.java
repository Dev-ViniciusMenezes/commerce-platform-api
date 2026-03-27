package com.viniciusdev.commerceapi.dto;

import java.math.BigDecimal;
import java.util.Set;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Set<CategoryResponse> categories
) {
}

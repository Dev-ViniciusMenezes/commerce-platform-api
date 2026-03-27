package com.viniciusdev.commerceapi.mapper;

import com.viniciusdev.commerceapi.database.model.Product;
import com.viniciusdev.commerceapi.dto.ProductRequest;
import com.viniciusdev.commerceapi.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final CategoryMapper categoryMapper;

    public Product toEntity (ProductRequest request) {
        Product product = new Product(null ,request.name(), request.description(), request.price());
        return product;
    }

    public ProductResponse toDTO (Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategories().stream()
                        .map(categoryMapper::toDTO)
                        .collect(Collectors.toSet())
        );
    }

    public Product toUpdate (Product product, ProductRequest request) {

        if (request.name() != null) {
            product.setName(request.name());
        }

        if (request.description() != null) {
            product.setDescription(request.description());
        }

        if (request.price() != null) {
            product.setPrice(request.price());
        }

        return product;
    }
}

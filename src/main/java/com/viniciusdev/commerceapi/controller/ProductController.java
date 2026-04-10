package com.viniciusdev.commerceapi.controller;

import com.viniciusdev.commerceapi.dto.ProductRequest;
import com.viniciusdev.commerceapi.dto.ProductResponse;
import com.viniciusdev.commerceapi.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Tag(name = "Products", description = "Management for products")
@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    @Operation(summary = "Get all products")
    @GetMapping
    @ResponseStatus (HttpStatus.OK)
    public List<ProductResponse> getAllProducts () {
        return productService.getAllProducts();
    }

    @Operation(summary = "Get a product by id")
    @GetMapping("/{id}")
    @ResponseStatus (HttpStatus.OK)
    public ProductResponse getProductById (@PathVariable Long id) {
        return productService.getProductById(id);
    }


    @Operation(summary = "Create a new product")
    @PostMapping
    @ResponseStatus (HttpStatus.CREATED)
    public ProductResponse createProduct (@RequestBody @Valid ProductRequest request) {
        return productService.createProduct(request);
    }


    @Operation(summary = "Update a product")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse updateProduct (@PathVariable Long id, @RequestBody @Valid ProductRequest request) {
        return productService.updateProduct(id, request);
    }

    @Operation (summary = "Delete a product")
    @DeleteMapping("/{id}")
    @ResponseStatus (HttpStatus.NO_CONTENT)
    public void deleteProduct (@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    @Operation (summary = "Add category a product")
    @PostMapping("/{productId}/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse addCategoriesToProduct (@PathVariable Long productId, @RequestBody @NotNull (message = "Category ids cannot be null") @Size(min = 1, message = "At least one category id is required") Set<Long> categoryIds) {
        return productService.addCategoriesToProduct(productId, categoryIds);
    }

    @Operation (summary = "Update categories a product")
    @PutMapping("/{productId}/categories")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse setCategoriesForProduct (@PathVariable Long productId, @RequestBody @NotNull (message = "Category ids cannot be null") @Size(min = 1, message = "At least one category id is required") Set<Long> categoryIds) {
        return productService.setCategoriesForProduct(productId, categoryIds);
    }

    @Operation (summary = "Remove category a product")
    @DeleteMapping("/{productId}/categories")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategoriesFromProducts (@PathVariable Long productId, @RequestBody @NotNull (message = "Category ids cannot be null") @Size(min = 1, message = "At least one category id is required") Set<Long> categoryIds) {
        productService.removeCategoriesFromProducts(productId, categoryIds);
    }


}

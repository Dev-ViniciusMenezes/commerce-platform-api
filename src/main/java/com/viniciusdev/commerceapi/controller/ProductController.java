package com.viniciusdev.commerceapi.controller;

import com.viniciusdev.commerceapi.dto.ProductRequest;
import com.viniciusdev.commerceapi.dto.ProductResponse;
import com.viniciusdev.commerceapi.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    @PostMapping
    @ResponseStatus (HttpStatus.CREATED)
    public ProductResponse createProduct (@RequestBody @Valid ProductRequest request) {
        return productService.createProduct(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse updateProduct (@PathVariable Long id, @RequestBody @Valid ProductRequest request) {
        return productService.updateProduct(id, request);
    }

    @GetMapping
    @ResponseStatus (HttpStatus.OK)
    public List<ProductResponse> getAllProducts () {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    @ResponseStatus (HttpStatus.OK)
    public ProductResponse getProductById (@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus (HttpStatus.NO_CONTENT)
    public void deleteProduct (@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    @PostMapping("/{productId}/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse addCategoriesToProduct (@PathVariable Long productId, @RequestBody @NotNull (message = "Category ids cannot be null") @Size(min = 1, message = "At least one category id is required") Set<Long> categoryIds) {
        return productService.addCategoriesToProduct(productId, categoryIds);
    }

    @PutMapping("/{productId}/categories")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse setCategoriesForProduct (@PathVariable Long productId, @RequestBody @NotNull (message = "Category ids cannot be null") @Size(min = 1, message = "At least one category id is required") Set<Long> categoryIds) {
        return productService.setCategoriesForProduct(productId, categoryIds);
    }

    @DeleteMapping("/{productId}/categories")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategoriesFromProducts (@PathVariable Long productId, @RequestBody @NotNull (message = "Category ids cannot be null") @Size(min = 1, message = "At least one category id is required") Set<Long> categoryIds) {
        productService.removeCategoriesFromProducts(productId, categoryIds);
    }


}

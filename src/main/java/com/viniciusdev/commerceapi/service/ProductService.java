package com.viniciusdev.commerceapi.service;

import com.viniciusdev.commerceapi.database.model.Category;
import com.viniciusdev.commerceapi.database.model.Product;
import com.viniciusdev.commerceapi.dto.ProductRequest;
import com.viniciusdev.commerceapi.dto.ProductResponse;
import com.viniciusdev.commerceapi.repository.CategoryRepository;
import com.viniciusdev.commerceapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategories()
        );
    }

    public List<ProductResponse> getAllProducts() {
        List <Product> products = productRepository.findAll();
        return products.stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getCategories()
                )).toList();
    }

    public void createProduct(ProductRequest request) {
        Product product = new Product(null, request.name(), request.description(), request.price());
        productRepository.save(product);
    }


    public void updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        productRepository.save(product);
    }


    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public ProductResponse addCategoriesToProduct(Long productId, Set<Long> categoryIds) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        categoryIds.forEach(categoryId -> {
            Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));
            product.addCategory(category);
        });
        productRepository.save(product);

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategories()
        );
    }

    public void removeCategoriesFromProducts(Long productId, Set<Long> categoryIds) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        categoryIds.forEach(categoryId -> {
            Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));
            product.removeCategory(category);
        });
        productRepository.save(product);
    }

    public ProductResponse setCategoriesForProduct(Long productId, Set<Long> categoryIds) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        product.getCategories().clear();
        categoryIds.forEach(categoryId -> {
            Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));
            product.addCategory(category);
        });
        productRepository.save(product);
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategories()
        );
    }




}


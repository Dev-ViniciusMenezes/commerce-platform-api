package com.viniciusdev.commerceapi.service;

import com.viniciusdev.commerceapi.database.model.Category;
import com.viniciusdev.commerceapi.database.model.Product;
import com.viniciusdev.commerceapi.dto.ProductRequest;
import com.viniciusdev.commerceapi.dto.ProductResponse;
import com.viniciusdev.commerceapi.mapper.ProductMapper;
import com.viniciusdev.commerceapi.database.repository.CategoryRepository;
import com.viniciusdev.commerceapi.database.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        return productMapper.toDTO(product);
    }

    public List<ProductResponse> getAllProducts() {
        List <Product> products = productRepository.findAll();
        return products.stream()
                .map(productMapper::toDTO).toList();
    }

    public ProductResponse createProduct(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        productRepository.save(product);
        return productMapper.toDTO(product);
    }


    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        productMapper.toUpdate(product,request);
        productRepository.save(product);
        return productMapper.toDTO(product);
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

        return productMapper.toDTO(product);
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
        return productMapper.toDTO(product);
    }
}


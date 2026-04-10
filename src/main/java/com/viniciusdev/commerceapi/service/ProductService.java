package com.viniciusdev.commerceapi.service;

import com.viniciusdev.commerceapi.database.model.Category;
import com.viniciusdev.commerceapi.database.model.Product;
import com.viniciusdev.commerceapi.dto.ProductRequest;
import com.viniciusdev.commerceapi.dto.ProductResponse;
import com.viniciusdev.commerceapi.exception.BusinessException;
import com.viniciusdev.commerceapi.exception.InvalidPriceException;
import com.viniciusdev.commerceapi.exception.ResourceNotFoundException;
import com.viniciusdev.commerceapi.mapper.ProductMapper;
import com.viniciusdev.commerceapi.database.repository.CategoryRepository;
import com.viniciusdev.commerceapi.database.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found " + id));
        return productMapper.toDTO(product);
    }

    public List<ProductResponse> getAllProducts() {
        List <Product> products = productRepository.findAll();
        return products.stream()
                .map(productMapper::toDTO).toList();
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        productRepository.save(product);
        return productMapper.toDTO(product);
    }


    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request){
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found " + id));
        productMapper.toUpdate(product,request);
        return productMapper.toDTO(product);
    }


    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (!product.getOrders().isEmpty()) {
            throw new BusinessException("Cannot delete a product with orders");
        }
        productRepository.delete(product);
    }

    @Transactional
    public ProductResponse addCategoriesToProduct(Long productId, Set<Long> categoryIds) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found " + productId));
        categoryIds.forEach(categoryId -> {
            Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found " + categoryId));
            product.addCategory(category);
        });
        return productMapper.toDTO(product);
    }

    @Transactional
    public void removeCategoriesFromProducts(Long productId, Set<Long> categoryIds) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found " + productId));
        categoryIds.forEach(categoryId -> {
            Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found " + categoryId));
            product.removeCategory(category);
        });
    }

    @Transactional
    public ProductResponse setCategoriesForProduct(Long productId, Set<Long> categoryIds) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found " + productId));
        product.getCategories().clear();
        categoryIds.forEach(categoryId -> {
            Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found " + categoryId));
            product.addCategory(category);
        });
        productRepository.save(product);
        return productMapper.toDTO(product);
    }
}


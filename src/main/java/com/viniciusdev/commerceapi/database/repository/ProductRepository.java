package com.viniciusdev.commerceapi.database.repository;


import com.viniciusdev.commerceapi.database.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}

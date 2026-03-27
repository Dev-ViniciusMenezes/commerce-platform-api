package com.viniciusdev.commerceapi.database.repository;

import com.viniciusdev.commerceapi.database.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}

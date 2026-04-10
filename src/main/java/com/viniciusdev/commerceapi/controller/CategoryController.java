package com.viniciusdev.commerceapi.controller;

import com.viniciusdev.commerceapi.dto.CategoryRequest;
import com.viniciusdev.commerceapi.dto.CategoryResponse;
import com.viniciusdev.commerceapi.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Categories", description = "Management for category")
@RestController
@RequestMapping ("/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;


    @Operation (summary = "Get all categories")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @Operation (summary = "Get a category by id")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryResponse getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }


    @Operation (summary = "Create a new category")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory (@RequestBody @Valid CategoryRequest request) {
        return categoryService.createCategory(request);
    }

    @Operation (summary = "Update a category")
    @PutMapping("/{id}")
    public CategoryResponse updateCategory (@PathVariable Long id, @RequestBody @Valid CategoryRequest request) {
        return categoryService.updateCategory(id, request);
    }

    @Operation (summary = "Delete a category")
    @DeleteMapping ("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory (@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }


}

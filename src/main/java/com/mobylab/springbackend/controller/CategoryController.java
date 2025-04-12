package com.mobylab.springbackend.controller;

import com.mobylab.springbackend.entity.Category;
import com.mobylab.springbackend.service.CategoryService;
import com.mobylab.springbackend.service.dto.CategoryDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/home/category")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    @SecurityRequirement(name = "bearerAuth")
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> addCategory(@RequestBody CategoryDto addedCategory) {
        logger.info("Request to add category {}", addedCategory);
        Category newCategory = categoryService.addCategory(addedCategory);
        return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
    }
}

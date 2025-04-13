package com.mobylab.springbackend.controller;

import com.mobylab.springbackend.entity.Category;
import com.mobylab.springbackend.exception.BadRequestException;
import com.mobylab.springbackend.service.CategoryService;
import com.mobylab.springbackend.service.dto.CategoryDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/home/categories")
public class CategoryController implements SecuredRestController{

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> addCategory(@RequestBody CategoryDto addedCategory) {
        logger.info("Request to add category {}", addedCategory);
        try {
            CategoryDto newCategory = new CategoryDto(categoryService.addCategory(addedCategory));
            return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
        } catch (BadRequestException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getAllCategories() {
        logger.info("Request to get all categories");
        List<CategoryDto> categories = categoryService.getAllCategories().stream().map(CategoryDto::new).toList();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @RequestMapping(path = "/{name}", method = RequestMethod.DELETE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteCategory(@PathVariable String name) {
        logger.info("Request to delete category {}", name);
        try {
            categoryService.deleteCategory(name);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (BadRequestException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}

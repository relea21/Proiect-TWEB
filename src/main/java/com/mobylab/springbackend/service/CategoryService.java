package com.mobylab.springbackend.service;

import com.mobylab.springbackend.entity.Category;
import com.mobylab.springbackend.exception.BadRequestException;
import com.mobylab.springbackend.repository.CategoryRepository;
import com.mobylab.springbackend.service.dto.CategoryDto;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    public Category addCategory(CategoryDto addedCategory) {
        if (addedCategory == null) {
            throw new BadRequestException("Category is null");
        }

        Category alreadyExists = categoryRepository.findByName(addedCategory.getName());

        if (alreadyExists != null) {
            throw new BadRequestException("Category with name '" + addedCategory.getName() + "' already exists.");
        }

        Category category = new Category();
        category.setName(addedCategory.getName());
        categoryRepository.save(category);

        logger.info("Category '{}' added successfully", addedCategory.getName());
        return category;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public void deleteCategory(String name) {
        Category category = categoryRepository.findByName(name);
        if (category == null) {
            throw new BadRequestException("Category with name '" + name + "' does not exist.");
        }
        categoryRepository.delete(category);
    }
}

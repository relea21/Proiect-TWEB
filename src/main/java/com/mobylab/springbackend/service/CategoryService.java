package com.mobylab.springbackend.service;

import com.mobylab.springbackend.entity.Bid;
import com.mobylab.springbackend.entity.Category;
import com.mobylab.springbackend.entity.Product;
import com.mobylab.springbackend.exception.BadRequestException;
import com.mobylab.springbackend.repository.BidRepository;
import com.mobylab.springbackend.repository.CategoryRepository;
import com.mobylab.springbackend.repository.ProductRepository;
import com.mobylab.springbackend.service.dto.CategoryDto;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BidRepository bidRepository;

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

        List<Product> products = new ArrayList<>(category.getProducts());

        for (Product p : products) {
            List<Bid> bidsToDelete= new ArrayList<>(p.getBidList());
            for (Bid b : bidsToDelete) {
                b.setProduct(null);
                bidRepository.delete(b);
            }
            bidRepository.flush();
            p.getBidList().clear();
            p.getUser().getProductsList().remove(p);
            p.getCategory().getProducts().remove(p);
            p.setCategory(null);
            p.setUser(null);
            productRepository.delete(p);
            productRepository.flush();
        }
        categoryRepository.delete(category);
        categoryRepository.flush();
    }
}

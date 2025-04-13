package com.mobylab.springbackend.service;

import com.mobylab.springbackend.entity.Category;
import com.mobylab.springbackend.entity.User;
import com.mobylab.springbackend.exception.BadRequestException;
import com.mobylab.springbackend.repository.CategoryRepository;
import com.mobylab.springbackend.repository.UserRepository;
import com.mobylab.springbackend.service.dto.CategoryDto;
import com.mobylab.springbackend.entity.Product;
import com.mobylab.springbackend.repository.ProductRepository;
import com.mobylab.springbackend.service.dto.ProductDto;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository) {
        this.productRepository = productRepository;
        this.categoryRepository  = categoryRepository;
        this.userRepository = userRepository;
    }

    public List<ProductDto> findByCategory(String categoryName) {
        Category category = categoryRepository.findByName(categoryName);
        if (category == null) {
            throw new BadRequestException("Category with name '" + categoryName + "' does not exist.");
        }
        return category.getProducts().stream().map(ProductDto::new).collect(Collectors.toList());
    }
    public ProductDto addProduct(ProductDto productDto, String categoryName) {
        Category category = categoryRepository.findByName(categoryName);
        if (category == null) {
            throw new BadRequestException("Category with name '" + categoryName + "' does not exist.");
        }
        User user = findUserLogged();
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setStartingPrice(productDto.getPrice());
        category.addProduct(product);
        user.addProduct(product);
        productRepository.save(product);
        return new ProductDto(product);
    }

    public void deleteProduct(String productName, String categoryName) {
        Category category = categoryRepository.findByName(categoryName);
        if (category == null) {
            throw new BadRequestException("Category with name '" + categoryName + "' does not exist.");
        }
        Product product = productRepository.findByName(productName);
        if (product == null) {
            throw new BadRequestException("Product with name '" + productName + "' does not exist.");
        }
        User user = findUserLogged();
        if (!user.equals(product.getUser()) && !checkIsAdmin(user)) {
            throw new BadRequestException("You do not have permission to delete this product.");
        }

        logger.info("Deleting product " + product.getName() + " " + product.getDescription() + " " + product.getStartingPrice());

        category.getProducts().remove(product);
        product.getUser().getProductsList().remove(product);
        product.setCategory(null);
        product.setUser(null);
        productRepository.delete(product);
    }

    public ProductDto getProduct(String productName, String categoryName) {
        Category category = categoryRepository.findByName(categoryName);
        if (category == null) {
            throw new BadRequestException("Category with name '" + categoryName + "' does not exist.");
        }
        Product product = productRepository.findByName(productName);
        if (product == null) {
            throw new BadRequestException("Product with name '" + productName + "' does not exist.");
        }
        return new ProductDto(product);
    }

    public void editProduct(ProductDto productDto, String categoryName, String productName) {
        Category category = categoryRepository.findByName(categoryName);
        if (category == null) {
            throw new BadRequestException("Category with name '" + categoryName + "' does not exist.");
        }
        Product product = productRepository.findByName(productName);
        if (product == null) {
            throw new BadRequestException("Product with name '" + productName + "' does not exist.");
        }
        User user = findUserLogged();
        if (!user.equals(product.getUser()) && !checkIsAdmin(user)) {
            throw new BadRequestException("You do not have permission to edit this product.");
        }
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setStartingPrice(productDto.getPrice());
        productRepository.save(product);

    }

    private User findUserLogged() {
        UserDetails userLogged = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userLogged.getUsername();
        return userRepository.findUserByEmail(email).get();
    }

    private boolean checkIsAdmin(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));
    }
}

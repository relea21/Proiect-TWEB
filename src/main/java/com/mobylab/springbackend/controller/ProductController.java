package com.mobylab.springbackend.controller;

import com.mobylab.springbackend.entity.Product;
import com.mobylab.springbackend.exception.BadRequestException;
import com.mobylab.springbackend.service.ProductService;
import com.mobylab.springbackend.service.dto.ProductDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/home/categories/{categoryName}/products")
public class ProductController implements SecuredRestController {
    @Autowired
    private ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> findByCategory(@PathVariable String categoryName) {
        logger.info("Products in the category {}", categoryName);
        try {
            List<ProductDto> products = productService.findByCategory(categoryName);
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch(BadRequestException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> addProduct(@RequestBody ProductDto productDto, @PathVariable String categoryName) {
        logger.info("Add product {}", productDto.getName());
        try {
            ProductDto newProduct = productService.addProduct(productDto, categoryName);
            return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
        } catch (BadRequestException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/{productName}")
    public ResponseEntity<?> deleteProduct(@PathVariable String productName, @PathVariable String categoryName) {
        logger.info("Delete product {}", productName);
        try {
            productService.deleteProduct(productName, categoryName);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (BadRequestException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/{productName}")
    public ResponseEntity<?> updateProduct(@PathVariable String productName,
                                           @RequestBody ProductDto productDto,
                                           @PathVariable String categoryName) {
        logger.info("Update product {}", productName);
        try {
            productService.editProduct(productDto, categoryName, productName);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (BadRequestException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @RequestMapping(method = RequestMethod.GET, path = "/{productName}")
    public ResponseEntity<?> getProduct(@PathVariable String productName,
                                            @PathVariable String categoryName) {
        logger.info("Get product {}", productName);
        try {
            ProductDto productDto = productService.getProduct(productName, categoryName);
            return new ResponseEntity<>(productDto, HttpStatus.OK);
        } catch(BadRequestException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


}

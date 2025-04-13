package com.mobylab.springbackend.service.dto;

import com.mobylab.springbackend.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductDto {
    private String name;
    private String description;
    private Integer price;

    public ProductDto() {}
    public ProductDto(Product product) {
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getStartingPrice();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}

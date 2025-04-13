package com.mobylab.springbackend.service.dto;

import com.mobylab.springbackend.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryDto {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryDto() {}
    public CategoryDto(String name) {
        this.name = name;
    }
    public CategoryDto(Category category) {
        this.name = category.getName();
    }

}

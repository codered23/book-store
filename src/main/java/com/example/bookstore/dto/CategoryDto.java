package com.example.bookstore.dto;

import lombok.Data;

@Data
public class CategoryDto {
    private Long id;
    private String name;
    private String description;
    private boolean isDeleted;
}

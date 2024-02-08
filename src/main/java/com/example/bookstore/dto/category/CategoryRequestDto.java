package com.example.bookstore.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequestDto {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private boolean isDeleted = false;
}

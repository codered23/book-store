package com.example.bookstore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreateBookRequestDto {
    @NotNull
    private String title;
    @NotNull
    private String author;
    @Min(0)
    private BigDecimal price;
    private String isbn;
    private String description;
    private String coverImage;
    private boolean isDeleted = false;
}

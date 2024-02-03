package com.example.bookstore.dto.book;

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
    @NotNull
    private BigDecimal price;
    @NotNull
    private String isbn;
    private String description;
    private String coverImage;
    private boolean isDeleted = false;
}

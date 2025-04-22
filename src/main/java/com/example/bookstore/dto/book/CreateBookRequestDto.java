package com.example.bookstore.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateBookRequestDto {
    @NotBlank
    private String title;
    @NotBlank
    private String author;
    @Min(0)
    @NotNull
    private BigDecimal price;
    @NotBlank
    private String isbn;
    private String description;
    private String coverImage;
    private boolean isDeleted = false;
}

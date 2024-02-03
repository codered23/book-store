package com.example.bookstore.dto.book;

import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;

@Data
public class BookDto {
    private Long id;
    private String title;
    private String author;
    private BigDecimal price;
    private String isbn;
    private String description;
    private String coverImage;
    private Set<Long> categoryIds;
    private boolean isDeleted;
}

package com.example.bookstore.dto.book;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookDto {
    private Long id;
    private String title;
    private String author;
    private BigDecimal price;
    private String isbn;
    private String description;
    private String coverImage;
    @Builder.Default
    private Set<Long> categoryIds = new HashSet<>();
    private boolean isDeleted;
}

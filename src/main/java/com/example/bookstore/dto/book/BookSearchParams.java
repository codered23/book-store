package com.example.bookstore.dto.book;

import java.math.BigDecimal;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BookSearchParams {
    private final String[] authors;
    private final BigDecimal minPrice;
    private final BigDecimal maxPrice;
}

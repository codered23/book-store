package com.example.bookstore.repository;

import com.example.bookstore.dto.book.BookSearchParams;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(BookSearchParams searchParameters);
}

package com.example.bookstore.repository;

import com.example.bookstore.dto.BookSearchParameters;
import com.example.bookstore.model.Book;
import com.example.bookstore.repository.specifications.AuthorSpecificationProvider;
import com.example.bookstore.repository.specifications.PriceSpecificationProvider;
import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    @Override
    public Specification<Book> build(BookSearchParameters searchParameters) {
        Specification<Book> spec = Specification.where(null);
        if (searchParameters.authors() != null && searchParameters.authors().length > 0) {
            spec = spec.and(AuthorSpecificationProvider
                    .getSpecification(searchParameters.authors()));

        }
        if (searchParameters.minPrice() != null && searchParameters.maxPrice() != null) {
            BigDecimal minPrice = searchParameters.minPrice();
            BigDecimal maxPrice = searchParameters.maxPrice();
            if (minPrice.compareTo(BigDecimal.ZERO) >= 0
                    && maxPrice.compareTo(BigDecimal.ZERO) >= 0) {
                spec = spec.and(PriceSpecificationProvider
                        .getSpecification(minPrice, maxPrice));
            } else {
                throw new RuntimeException("Invalid price range");
            }
        }
        return spec;
    }
}

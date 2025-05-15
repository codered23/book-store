package com.example.bookstore.repository.specifications;

import com.example.bookstore.dto.book.BookSearchParams;
import com.example.bookstore.model.Book;
import com.example.bookstore.repository.specifications.provider.AuthorSpecificationProvider;
import com.example.bookstore.repository.specifications.provider.PriceSpecificationProvider;
import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    @Override
    public Specification<Book> build(BookSearchParams searchParameters) {
        Specification<Book> spec = Specification.where(null);
        if (searchParameters.getAuthors() != null && searchParameters.getAuthors().length > 0) {
            spec = spec.and(AuthorSpecificationProvider
                    .getSpecification(searchParameters.getAuthors()));
        }
        if (searchParameters.getMinPrice() != null && searchParameters.getMaxPrice() != null) {
            BigDecimal minPrice = searchParameters.getMinPrice();
            BigDecimal maxPrice = searchParameters.getMaxPrice();
            if (minPrice.compareTo(BigDecimal.ZERO) >= 0
                    && maxPrice.compareTo(BigDecimal.ZERO) >= 0
                    && maxPrice.compareTo(minPrice) >= 0) {
                spec = spec.and(PriceSpecificationProvider
                        .getSpecification(minPrice, maxPrice));
            } else {
                throw new RuntimeException("Invalid price range");
            }
        }
        return spec;
    }
}

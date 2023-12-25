package com.example.bookstore.repository.specifications;

import com.example.bookstore.model.Book;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AuthorSpecificationProvider {
    public static Specification<Book> getSpecification(String[] authors) {
        String author = "author";
        return (root, query, criteriaBuilder) ->
             root.get(author).in(Arrays.stream(authors).toArray());
    }
}

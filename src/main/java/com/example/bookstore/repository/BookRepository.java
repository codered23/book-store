package com.example.bookstore.repository;

import com.example.bookstore.model.Book;
import java.util.List;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    @Override
    <S extends Book> List<S> findAll(Example<S> example, Sort sort);
}

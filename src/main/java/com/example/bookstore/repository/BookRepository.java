package com.example.bookstore.repository;

import com.example.bookstore.model.Book;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    Page<Book> findAll(Specification<Book> specification, Pageable pageable);

    List<Book> findAllByCategoriesId(Long categoryId);
}

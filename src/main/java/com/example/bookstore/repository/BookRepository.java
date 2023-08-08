package com.example.bookstore.repository;

import com.example.bookstore.model.Book;
import java.util.List;

public interface BookRepository {
    Book save(Book product);

    List<Book> findAll();
}

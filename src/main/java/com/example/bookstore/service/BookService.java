package com.example.bookstore.service;

import com.example.bookstore.model.Book;
import java.util.List;

public interface BookService {
    Book save(Book product);

    List<Book> findAll();
}

package com.example.bookstore.service;

import com.example.bookstore.dto.BookDto;
import com.example.bookstore.dto.BookSearchParams;
import com.example.bookstore.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto product);

    List<BookDto> findAll();

    BookDto getById(Long id);

    void deleteById(Long id);

    BookDto update(Long id, CreateBookRequestDto requestDto);

    List<BookDto> search(BookSearchParams params);
}

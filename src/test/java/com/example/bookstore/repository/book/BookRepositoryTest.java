package com.example.bookstore.repository.book;

import com.example.bookstore.model.Book;
import com.example.bookstore.model.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class BookRepositoryTest {
    private BookRepository bookRepository;

    @Autowired
    void setBookRepository(BookRepository b) {
        bookRepository = b;
    }

    @BeforeEach
    void setUp() {
        Book book = new Book();
        book.setTitle("3 comrades");
        book.setAuthor("Erich Maria Remark");
        book.setIsbn("2341234");
        book.setDescription("Story about 3 friends");
        book.setPrice(BigDecimal.valueOf(250));
        book.setCoverImage("some image");
        bookRepository.save(book);
    }

    @Test
    void findAll() {

        List<Book> all = bookRepository.findAll();
        int expected = 1;
        Assertions.assertEquals(1, all.size());
    }

    @Test
    void findAllByCategoriesId() {
    }
}
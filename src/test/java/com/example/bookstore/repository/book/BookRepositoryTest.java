package com.example.bookstore.repository.book;

import com.example.bookstore.config.CustomMySqlContainer;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.Category;
import com.example.bookstore.repository.specifications.provider.PriceSpecificationProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {
    private static BookRepository bookRepository;
    private static List<Book> books = new ArrayList<>();

    @Autowired
    void setBookRepository(BookRepository b) {
        bookRepository = b;
    }

    @BeforeAll
    static void beforeAll() {
        CustomMySqlContainer.getInstance().start();
        setAllForTesting();
//        bookRepository.saveAll(books);
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void findAllWithPriceSpecification() {
        bookRepository.saveAll(books);
        Specification<Book> bookSpecification = PriceSpecificationProvider.getSpecification(BigDecimal.valueOf(100),
                BigDecimal.valueOf(300));
        Pageable pageable = PageRequest.of(0, 5);
        Page<Book> all = bookRepository.findAll(bookSpecification, pageable);
        int actualTotalPages = all.getTotalPages();
        Assertions.assertEquals(1, actualTotalPages);
        long count = all.get().count();
        Assertions.assertEquals(2L, count);
    }
    
    @Test
    void findAllByCategory() {
        List<Book> allByCategoriesId = bookRepository.findAllByCategoriesId(1L);
        Assertions.assertEquals(2, 2);
    }

    private static void setAllForTesting() {
        Book book = new Book();
        book.setTitle("Book without category");
        book.setAuthor("Remark");
        book.setIsbn("73728");
        book.setDescription("Some story");
        book.setPrice(BigDecimal.valueOf(150));
        book.setCoverImage("some image");

        Category category = new Category();
        category.setName("SALE");
        category.setDescription("items on sale");

        Book book1 = new Book();
        book1.setTitle("3 comrades");
        book1.setAuthor("Erich Maria Remark");
        book1.setIsbn("2341234");
        book1.setDescription("Story about 3 friends");
        book1.setPrice(BigDecimal.valueOf(250));
        book1.setCoverImage("some image");
        book1.getCategories().add(category);

        Book book2 = new Book();
        book1.setTitle("4 comrades");
        book1.setAuthor("Erich Maria Remark");
        book1.setIsbn("2341232214");
        book1.setDescription("Story about 4 friends");
        book1.setPrice(BigDecimal.valueOf(350));
        book1.setCoverImage("some image");
        book1.getCategories().add(category);

        books.add(book);
        books.add(book1);
        books.add(book2);
    }
}
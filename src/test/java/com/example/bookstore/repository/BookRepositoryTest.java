package com.example.bookstore.repository;

import com.example.bookstore.config.CustomMySqlContainer;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.Category;
import com.example.bookstore.repository.specifications.provider.PriceSpecificationProvider;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
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
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {
    private final BookRepository bookRepository;
    private final  CategoryRepository categoryRepository;
    private Long romanCategoryId;
    private Long fantasyCategoryId;
    private final List<Book> books = new ArrayList<>();
    private static final CustomMySqlContainer container = CustomMySqlContainer.getInstance();

    @Autowired
    public BookRepositoryTest(BookRepository bookRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
    }

    @BeforeAll
    static void beforeAll() {
        container.start();
    }

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        categoryRepository.deleteAll();

        Category category = new Category();
        category.setName("Roman");
        category.setDescription("the story about 3 friends");
        romanCategoryId = categoryRepository.save(category).getId();

        Category category2 = new Category();
        category2.setName("Fantasy");
        category2.setDescription("Magic and adventure");
        fantasyCategoryId = categoryRepository.save(category2).getId();

        Book book1 = createBook("First Book", category, BigDecimal.valueOf(11.00));
        Book book2 = createBook("Second Book", category2, BigDecimal.valueOf(15.50));
        Book book3 = createBook("Third Book", category2, BigDecimal.valueOf(20.00));

        books.addAll(List.of(book1, book2, book3));
    }

    private Book createBook(String title, Category category, BigDecimal price) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor("Some Author");
        book.setIsbn(UUID.randomUUID().toString());
        book.setDescription("Some Description");
        book.setPrice(price);
        book.setCoverImage("some_image.jpg");
        book.setCategories(new HashSet<>(List.of(category)));
        return bookRepository.save(book);
    }
    @Test
    @DisplayName("Finding all books with price specification returns the correct book")
    void findAllWithPriceSpecification() {
        Specification<Book> bookSpecification = PriceSpecificationProvider.getSpecification(BigDecimal.valueOf(10),
                BigDecimal.valueOf(12));
        Pageable pageable = PageRequest.of(0, 5);
        Page<Book> all = bookRepository.findAll(bookSpecification, pageable);

        int actualTotalPages = all.getTotalPages();
        Assertions.assertEquals(1, actualTotalPages);
        long actualCountOfBooks = all.get().count();
        Assertions.assertEquals(1L, actualCountOfBooks);
        Assertions.assertTrue(all.get().anyMatch(dto -> EqualsBuilder.reflectionEquals(dto, books.get(0))));
    }
    
    @Test
    @DisplayName("Finding all books by Roman category ID returns the correct books")
    void findAllByRomanCategoryId() {
        List<Book> allByCategoriesId = bookRepository.findAllByCategoriesId(romanCategoryId);
        Assertions.assertEquals(1, allByCategoriesId.size());
        Assertions.assertEquals(allByCategoriesId.get(0).getTitle(), "First Book");
    }

    @Test
    @DisplayName("Finding all books by Fantasy category ID returns the correct book")
    void findAllByFantasyCategoryId() {
        List<Book> allByCategoriesId = bookRepository.findAllByCategoriesId(fantasyCategoryId);
        Assertions.assertEquals(2, allByCategoriesId.size());
        Assertions.assertTrue(allByCategoriesId.stream().anyMatch(book -> book.getTitle().equals("Second Book")));
        Assertions.assertTrue(allByCategoriesId.stream().anyMatch(book -> book.getTitle().equals("Third Book")));
    }
}
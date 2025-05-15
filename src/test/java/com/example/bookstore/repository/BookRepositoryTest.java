package com.example.bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

import com.example.bookstore.config.CustomMySqlContainer;
import com.example.bookstore.dto.book.CreateBookRequestDto;
import com.example.bookstore.model.Book;
import com.example.bookstore.repository.specifications.provider.AuthorSpecificationProvider;
import com.example.bookstore.repository.specifications.provider.PriceSpecificationProvider;
import com.example.bookstore.util.TestUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"/sql/clean-up.sql",
        "/sql/create-default-categories.sql",
        "/sql/create-default-books.sql",
        "/sql/add-default-book-categories.sql"})
@Sql(scripts = "/sql/clean-up.sql", executionPhase = AFTER_TEST_METHOD)
class BookRepositoryTest {
    private static final CustomMySqlContainer container = CustomMySqlContainer.getInstance();
    private final BookRepository bookRepository;
    private final List<Book> books = new ArrayList<>();

    @Autowired
    public BookRepositoryTest(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @BeforeAll
    static void beforeAll() {
        container.start();
    }

    @BeforeEach
    void setUp() {
        CreateBookRequestDto firstRequestDto = TestUtil.createBookRequestDto(
                "Special case", "First Author", 20);
        CreateBookRequestDto secondRequestDto = TestUtil.createBookRequestDto(
                "Second Book", "Second Author", 30);
        CreateBookRequestDto thirdRequestDto = TestUtil.createBookRequestDto(
                "Third Book", "Third Author", 15);
        Book firstBook = TestUtil.createBook(firstRequestDto);
        Book secondBook = TestUtil.createBook(secondRequestDto);
        Book thirdBook = TestUtil.createBook(thirdRequestDto);
        books.addAll(List.of(firstBook, secondBook, thirdBook));
    }

    @Test
    @DisplayName("Finding all books with price specification returns the correct book")
    void findAllWithValidPriceSpecification() {
        Specification<Book> bookSpecification = PriceSpecificationProvider.getSpecification(
                BigDecimal.valueOf(10), BigDecimal.valueOf(17));
        Pageable pageable = PageRequest.of(0, 5);

        Page<Book> all = bookRepository.findAll(bookSpecification, pageable);

        int actualTotalPages = all.getTotalPages();
        assertEquals(1, actualTotalPages);
        Long actualCountOfBooks = all.get().count();
        assertEquals(1L, actualCountOfBooks);
        int indexOfBook = 2;
        assertTrue(all.get().anyMatch(dto ->
                EqualsBuilder.reflectionEquals(dto, books.get(indexOfBook),
                        "id", "isbn", "categories")));
    }

    @Test
    @DisplayName("Finding all books by Popular category ID returns the correct books")
    void findAllByValidCategoryId() {
        Long popularCategoryId = 3L;

        List<Book> allByCategoriesId = bookRepository.findAllByCategoriesId(popularCategoryId);

        assertEquals(3, allByCategoriesId.size());
        for (Book book : books) {
            assertTrue(allByCategoriesId.stream()
                    .anyMatch(e -> EqualsBuilder.reflectionEquals(e, book,
                            "id", "isbn", "categories")));
        }
    }

    @Test
    @DisplayName("Finding all books by Fantasy category ID returns the correct book")
    void findAllByNoValidCategoryId() {
        Long adventureCategoryId = 5L;

        List<Book> allByCategoriesId = bookRepository.findAllByCategoriesId(
                adventureCategoryId);

        assertTrue(allByCategoriesId.isEmpty());
    }

    @Test
    @DisplayName("Finding all books with author specification returns correct books")
    void findAllWithValidAuthorSpecification() {
        String[] authors = new String[]{"First Author"};
        Specification<Book> bookSpecification = AuthorSpecificationProvider
                .getSpecification(authors);
        Pageable pageable = PageRequest.of(0, 5);

        Page<Book> all = bookRepository.findAll(bookSpecification, pageable);

        int actualTotalPages = all.getTotalPages();
        assertEquals(1, actualTotalPages);
        Long actualCountOfBooks = all.get().count();
        assertEquals(1L, actualCountOfBooks);
        int indexOfBook = 0;
        assertTrue(all.get().anyMatch(dto ->
                EqualsBuilder.reflectionEquals(dto, books.get(indexOfBook),
                        "id", "isbn", "categories")));
    }
}

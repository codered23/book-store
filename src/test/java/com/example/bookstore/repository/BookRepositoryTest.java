package com.example.bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.bookstore.config.CustomMySqlContainer;
import com.example.bookstore.dto.book.CreateBookRequestDto;
import com.example.bookstore.dto.category.CategoryRequestDto;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.Category;
import com.example.bookstore.repository.specifications.provider.PriceSpecificationProvider;
import com.example.bookstore.util.TestUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
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
@Sql(scripts = "/sql/clean-up.sql")
class BookRepositoryTest {
    private static final CustomMySqlContainer container = CustomMySqlContainer.getInstance();
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final List<Book> books = new ArrayList<>();
    private Long romanCategoryId;
    private Long fantasyCategoryId;

    @Autowired
    public BookRepositoryTest(BookRepository bookRepository,
                              CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
    }

    @BeforeAll
    static void beforeAll() {
        container.start();
    }

    @BeforeEach
    void setUp() {
        CategoryRequestDto requestDto = TestUtil.createCategoryRequestDto("Roman");
        Category firstCategory = TestUtil.createCategory(requestDto);
        romanCategoryId = categoryRepository.save(firstCategory).getId();

        CategoryRequestDto secondRequestDto = TestUtil.createCategoryRequestDto("Fantasy");
        Category secondCategory = TestUtil.createCategory(secondRequestDto);

        fantasyCategoryId = categoryRepository.save(secondCategory).getId();

        Book firstBook = createBook("First Book", "Joshua", firstCategory, 11);
        Book secondBook = createBook("Second Book", "Bloch", secondCategory, 15);
        Book thirdBook = createBook("Third Book", "Artur", secondCategory, 20);

        books.addAll(List.of(firstBook, secondBook, thirdBook));
    }

    private Book createBook(String title, String author, Category category, int price) {
        CreateBookRequestDto bookRequestDto = TestUtil.createBookRequestDto(title, author, price);
        Book book = TestUtil.createBook(bookRequestDto);
        book.setCategories(new HashSet<>(List.of(category)));
        return bookRepository.save(book);
    }

    @Test
    @DisplayName("Finding all books with price specification returns the correct book")
    void findAllWithPriceSpecification() {
        Specification<Book> bookSpecification = PriceSpecificationProvider.getSpecification(
                BigDecimal.valueOf(10), BigDecimal.valueOf(12));
        Pageable pageable = PageRequest.of(0, 5);
        Page<Book> all = bookRepository.findAll(bookSpecification, pageable);

        int actualTotalPages = all.getTotalPages();
        assertEquals(1, actualTotalPages);
        long actualCountOfBooks = all.get().count();
        assertEquals(1L, actualCountOfBooks);
        assertTrue(all.get().anyMatch(dto -> EqualsBuilder.reflectionEquals(dto, books.get(0))));
    }

    @Test
    @DisplayName("Finding all books by Roman category ID returns the correct books")
    void findAllByRomanCategoryId() {
        List<Book> allByCategoriesId = bookRepository.findAllByCategoriesId(romanCategoryId);
        assertEquals(1, allByCategoriesId.size());
        assertEquals(allByCategoriesId.get(0).getTitle(), "First Book");
    }

    @Test
    @DisplayName("Finding all books by Fantasy category ID returns the correct book")
    void findAllByFantasyCategoryId() {
        List<Book> allByCategoriesId = bookRepository.findAllByCategoriesId(fantasyCategoryId);
        assertEquals(2, allByCategoriesId.size());
        assertTrue(allByCategoriesId.stream().anyMatch(book ->
                book.getTitle().equals("Second Book")));
        assertTrue(allByCategoriesId.stream().anyMatch(book ->
                book.getTitle().equals("Third Book")));
    }
}

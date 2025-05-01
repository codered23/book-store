package com.example.bookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bookstore.config.CustomMySqlContainer;
import com.example.bookstore.dto.book.BookDto;
import com.example.bookstore.dto.book.CreateBookRequestDto;
import com.example.bookstore.dto.category.CategoryDto;
import com.example.bookstore.dto.category.CategoryRequestDto;
import com.example.bookstore.service.BookService;
import com.example.bookstore.service.CategoryService;
import com.example.bookstore.util.TestUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/clean-up.sql")
public class BookControllerTest {
    private static final CustomMySqlContainer container = CustomMySqlContainer.getInstance();
    private static final String BOOKS_API_URL = "/api/books";
    private static final String BOOKS_BY_ID_API_URL = "/api/books/{id}";
    private static final String BOOKS_SEARCH_API_URL = "/api/books/search";
    private static final String ADD_BOOK_TO_CATEGORY_API_URL =
            "/api/books/{bookId}/categories/{categoryId}";
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final BookService bookService;
    private final CategoryService categoryService;

    @Autowired
    public BookControllerTest(MockMvc mockMvc, ObjectMapper objectMapper,
                              BookService bookService, CategoryService categoryService) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.bookService = bookService;
        this.categoryService = categoryService;
    }

    @BeforeAll
    static void beforeAll() {
        container.start();
    }

    private BookDto createBookFromDto(CreateBookRequestDto requestDto) {
        BookDto savedBook = bookService.save(requestDto);
        savedBook.setPrice(savedBook.getPrice().setScale(2, RoundingMode.HALF_UP));
        return savedBook;
    }

    @Test
    @DisplayName("Creating a new book with valid data returns BookDto with Created status")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createNewBook_Valid_ReturnsBookDto() throws Exception {
        CreateBookRequestDto requestDto = TestUtil.createBookRequestDto("First book", "Author", 25);
        BookDto expected = BookDto.builder()
                .title(requestDto.getTitle())
                .author(requestDto.getAuthor())
                .isbn(requestDto.getIsbn())
                .description(requestDto.getDescription())
                .price(requestDto.getPrice())
                .coverImage(requestDto.getCoverImage())
                .isDeleted(requestDto.isDeleted())
                .build();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(post(BOOKS_API_URL)
                        .contentType(APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andReturn();
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @Test
    @DisplayName("Getting a book by a valid ID returns BookDto with Ok status")
    @WithMockUser(username = "user")
    void getBookById_ValidId_ReturnsDto() throws Exception {
        CreateBookRequestDto requestDto = TestUtil
                .createBookRequestDto("First book", "Author", 25);
        BookDto expected = createBookFromDto(requestDto);
        Long id = expected.getId();
        MvcResult result = mockMvc.perform(get(BOOKS_BY_ID_API_URL, id))
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("Getting all books returns a list of BookDto with Ok status")
    @WithMockUser(username = "user")
    void getAllBooks_Valid_ReturnsListOfBookDto() throws Exception {
        final CreateBookRequestDto firstRequestDto = TestUtil
                .createBookRequestDto("First book", "Author", 25);
        final BookDto firstBookDto = createBookFromDto(firstRequestDto);
        final CreateBookRequestDto secondRequestDto = TestUtil
                .createBookRequestDto("First book", "Author", 30);
        final BookDto secondBookDto = createBookFromDto(secondRequestDto);
        List<BookDto> expected = List.of(firstBookDto, secondBookDto);
        Pageable pageable = Pageable.ofSize(10);
        MvcResult result = mockMvc.perform(get(BOOKS_API_URL)
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize())))
                .andExpect(status().isOk())
                .andReturn();
        List<BookDto> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<BookDto>>() {});
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected) && expected.containsAll(actual));
    }

    @Test
    @DisplayName("Deleting a book by a valid ID returns NoContent status and the book is not found")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteBook_ValidID() throws Exception {
        CreateBookRequestDto requestDto = TestUtil
                .createBookRequestDto("First book", "Author", 25);
        BookDto bookFromDto = createBookFromDto(requestDto);
        Long idToDelete = bookFromDto.getId();
        mockMvc.perform(delete(BOOKS_BY_ID_API_URL, idToDelete))
                .andExpect(status().isNoContent());
        mockMvc.perform(get(BOOKS_BY_ID_API_URL, idToDelete)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
        MvcResult getAllResult = mockMvc.perform(get(BOOKS_API_URL))
                .andExpect(status().isOk())
                .andReturn();
        List<BookDto> actualAllBooks = objectMapper.readValue(
                getAllResult.getResponse().getContentAsString(),
                new TypeReference<List<BookDto>>() {});
        assertTrue(actualAllBooks.stream()
                        .noneMatch(b -> b.getId().equals(idToDelete)),
                "Deleted book should not be present in the list of all books.");
    }

    @Test
    @DisplayName("Searching books by valid parameters returns"
            + " a list of matching BookDto with Ok status")
    @WithMockUser(username = "user")
    void searchBookByParams_Valid() throws Exception {
        CreateBookRequestDto requestDto = TestUtil
                .createBookRequestDto("First book", "FirstAuthor", 11);
        CreateBookRequestDto requestDto2 = TestUtil
                .createBookRequestDto("Second book", "SecondAuthor", 21);
        final BookDto firstBookDto = createBookFromDto(requestDto);
        final BookDto secondBook = createBookFromDto(requestDto2);
        BigDecimal minPrice = BigDecimal.valueOf(10);
        BigDecimal maxPrice = BigDecimal.valueOf(20);
        MvcResult firstResult = mockMvc.perform(get(BOOKS_SEARCH_API_URL)
                        .param("minPrice", minPrice.toString())
                        .param("maxPrice", maxPrice.toString()))
                .andExpect(status().isOk())
                .andReturn();
        MvcResult secondResult = mockMvc.perform(get(BOOKS_SEARCH_API_URL)
                        .param("authors", requestDto2.getAuthor()))
                .andExpect(status().isOk())
                .andReturn();
        List<BookDto> firstActualList = objectMapper.readValue(
                firstResult.getResponse().getContentAsString(),
                new TypeReference<List<BookDto>>() {});
        firstActualList.forEach(dto ->
                dto.setPrice(dto.getPrice().setScale(2, RoundingMode.HALF_UP)));
        List<BookDto> secondActualList = objectMapper.readValue(
                secondResult.getResponse().getContentAsString(),
                new TypeReference<List<BookDto>>() {});
        secondActualList.forEach(dto ->
                dto.setPrice(dto.getPrice().setScale(2, RoundingMode.HALF_UP)));
        assertEquals(1L, firstActualList.size());
        assertTrue(EqualsBuilder.reflectionEquals(firstBookDto, firstActualList.get(0)));
        assertEquals(1L, secondActualList.size());
        assertEquals(secondBook.getAuthor(), secondActualList.get(0).getAuthor(),
                "Authors should match");
        assertEquals(0, secondBook.getPrice().compareTo(secondActualList.get(0).getPrice()),
                "Prices should match");
        assertEquals(secondBook.getTitle(), secondActualList.get(0).getTitle(),
                "Titles should match");
        assertTrue(EqualsBuilder.reflectionEquals(secondBook, secondActualList.get(0)));
    }

    @Test
    @DisplayName("Updating a book by a valid ID returns the updated BookDto with Ok status")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateBookById_Valid_ReturnsUpdatedBookDto() throws Exception {
        CreateBookRequestDto requestDto = TestUtil
                .createBookRequestDto("First book", "Author", 25);
        BookDto savedBookDto = createBookFromDto(requestDto);
        Long id = savedBookDto.getId();
        requestDto.setPrice(BigDecimal.valueOf(15));
        requestDto.setDescription("new description");
        BookDto expectedUpdatedBookDto = BookDto.builder()
                .id(savedBookDto.getId())
                .title(savedBookDto.getTitle())
                .author(savedBookDto.getAuthor())
                .isbn(savedBookDto.getIsbn())
                .description("new description")
                .price(BigDecimal.valueOf(15))
                .coverImage(savedBookDto.getCoverImage())
                .isDeleted(savedBookDto.isDeleted())
                .build();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put(BOOKS_BY_ID_API_URL, id)
                        .contentType(APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        assertTrue(EqualsBuilder.reflectionEquals(expectedUpdatedBookDto, actual));
        assertEquals(BigDecimal.valueOf(15), actual.getPrice());
        assertEquals("new description", actual.getDescription());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Adding a book to a category, controller returns updated BookDto")
    void addBookToCategory_ValidIds_ReturnsUpdateBookDto() throws Exception {
        CreateBookRequestDto request = TestUtil.createBookRequestDto("First book", "Author", 25);
        BookDto bookDto = createBookFromDto(request);
        CategoryRequestDto requestDto = TestUtil.createCategoryRequestDto("Adventure");
        CategoryDto categoryDto = categoryService.save(requestDto);

        MvcResult result = mockMvc.perform(post(ADD_BOOK_TO_CATEGORY_API_URL,
                        bookDto.getId(), categoryDto.getId()))
                .andExpect(status().isOk())
                .andReturn();

        BookDto updatedBookDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        assertTrue(EqualsBuilder.reflectionEquals(bookDto, updatedBookDto, "categoryIds"));
        assertNotNull(updatedBookDto.getCategoryIds());
        assertTrue(updatedBookDto.getCategoryIds().contains(categoryDto.getId()),
                "Book must contain the added category id");
    }
}

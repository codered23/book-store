package com.example.bookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bookstore.config.CustomMySqlContainer;
import com.example.bookstore.dto.book.BookDto;
import com.example.bookstore.dto.book.CreateBookRequestDto;
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
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"/sql/clean-up.sql",
        "/sql/create-default-categories.sql",
        "/sql/create-default-books.sql"})
@Sql(scripts = "/sql/clean-up.sql", executionPhase = AFTER_TEST_METHOD)
public class BookControllerTest {
    private static final CustomMySqlContainer container = CustomMySqlContainer.getInstance();
    private static final String BOOKS_API_URL = "/api/books";
    private static final String BOOKS_BY_ID_API_URL = "/api/books/{id}";
    private static final String BOOKS_SEARCH_API_URL = "/api/books/search";
    private static final String ADD_BOOK_TO_CATEGORY_API_URL =
            "/api/books/{bookId}/categories/{categoryId}";
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public BookControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @BeforeAll
    static void beforeAll() {
        container.start();
    }

    @Test
    @DisplayName("Creating a new book with valid data returns BookDto with Created status")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createBook_Valid_ReturnsBookDto() throws Exception {
        CreateBookRequestDto requestDto = TestUtil.createBookRequestDto(
                "First book", "Author", 25);
        BookDto expected = TestUtil.createBookDtoFromRequest(requestDto);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post(BOOKS_API_URL)
                        .contentType(APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);

        assertNotNull(actual.getId(), "Book ID should not be null after creation");
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id", "isbn"),
                String.format("expected BookDto: %s, but got %s", expected, actual));
    }

    @Test
    @DisplayName("Creating a book with null title returns 400 Bad Request")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createBook_NullTitle_ReturnsBadRequest() throws Exception {
        CreateBookRequestDto requestDto = TestUtil.createBookRequestDto(
                null, "First Author", 25);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post(BOOKS_API_URL)
                        .contentType(APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertTrue(responseBody.contains("BAD_REQUEST"), "Response should contain BAD_REQUEST");
    }

    @Test
    @DisplayName("Getting a book by a valid ID returns BookDto with Ok status")
    @WithMockUser(username = "user")
    void getBookById_ValidId_ReturnsDto() throws Exception {
        CreateBookRequestDto requestDto = TestUtil
                .createBookRequestDto("Special case", "First Author", 20);
        BookDto expected = TestUtil.createBookDtoFromRequest(requestDto);

        Long id = 100L;
        MvcResult result = mockMvc.perform(get(BOOKS_BY_ID_API_URL, id))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id", "isbn"),
                String.format("expected BookDto: %s, but got: %s", expected, actual));
    }

    @Test
    @DisplayName("Getting all books returns a list of BookDto with Ok status")
    @WithMockUser(username = "user")
    void getAllBooks_Valid_ReturnsListOfBookDto() throws Exception {
        CreateBookRequestDto firstRequestDto = TestUtil
                .createBookRequestDto("Second Book", "Second Author", 30);
        final BookDto firstBookDto = TestUtil.createBookDtoFromRequest(firstRequestDto);
        final CreateBookRequestDto secondRequestDto = TestUtil
                .createBookRequestDto("Special case", "First Author", 20);
        final CreateBookRequestDto thirdRequestDto = TestUtil
                .createBookRequestDto("Third Book", "Third Author", 15);
        final BookDto secondBookDto = TestUtil.createBookDtoFromRequest(secondRequestDto);
        List<BookDto> includedList = List.of(firstBookDto, secondBookDto);

        Pageable pageable = Pageable.ofSize(10);
        MvcResult result = mockMvc.perform(get(BOOKS_API_URL)
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize())))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDto> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<BookDto>>() {});
        Long expectedSizeOfList = 3L;
        assertEquals(expectedSizeOfList, actual.size());
        for (BookDto expectedBook : includedList) {
            assertTrue(actual.stream().anyMatch(e -> EqualsBuilder
                    .reflectionEquals(expectedBook, e, "id", "isbn")),
                    String.format("Expected BookDto was not found in the actual list: %s",
                            expectedBook));
        }
    }

    @Test
    @DisplayName("Deleting a book by a valid ID returns NoContent status and the book is not found")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteBook_ValidID() throws Exception {
        Long idToDelete = 11L;

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
                "Deleted book should not be present in the list of all books");
    }

    @Test
    @DisplayName("Searching books by author"
            + " return list of matching BookDto with Ok status")
    @WithMockUser(username = "user")
    void searchBookByAuthor_Valid() throws Exception {
        CreateBookRequestDto requestDto2 = TestUtil
                .createBookRequestDto("Special case", "First Author", 20);
        final BookDto expectedBookDto = TestUtil.createBookDtoFromRequest(requestDto2);

        MvcResult result = mockMvc.perform(get(BOOKS_SEARCH_API_URL)
                        .param("authors", requestDto2.getAuthor()))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDto> actualList = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<BookDto>>() {});
        actualList.forEach(dto ->
                dto.setPrice(dto.getPrice().setScale(2, RoundingMode.HALF_UP)));
        Long expectedSizeOfSecondList = 1L;
        assertEquals(expectedSizeOfSecondList, actualList.size());
        assertTrue(EqualsBuilder.reflectionEquals(
                expectedBookDto, actualList.get(0), "id", "isbn"),
                String.format("expected BookDto: %s, but got %s",
                        expectedBookDto, actualList.get(0)));
    }

    @Test
    @DisplayName("Searching books by prices return"
            + "list of matching BookDto with Ok status")
    @WithMockUser(username = "user")
    void searchBooksByPrice() throws Exception {
        CreateBookRequestDto firstRequestDto = TestUtil
                .createBookRequestDto("Third Book", "Third Author", 15);
        CreateBookRequestDto secondRequestDto = TestUtil
                .createBookRequestDto("Special case", "First Author", 20);
        final BookDto firstBookDto = TestUtil.createBookDtoFromRequest(firstRequestDto);
        final BookDto secondBookDto = TestUtil.createBookDtoFromRequest(secondRequestDto);
        List<BookDto> expectedList = List.of(firstBookDto, secondBookDto);
        BigDecimal minPrice = BigDecimal.valueOf(9);
        BigDecimal maxPrice = BigDecimal.valueOf(21);

        MvcResult firstResult = mockMvc.perform(get(BOOKS_SEARCH_API_URL)
                        .param("minPrice", minPrice.toString())
                        .param("maxPrice", maxPrice.toString()))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDto> actualList = objectMapper.readValue(
                firstResult.getResponse().getContentAsString(),
                new TypeReference<List<BookDto>>() {
                });
        actualList.forEach(dto ->
                dto.setPrice(dto.getPrice().setScale(2, RoundingMode.HALF_UP)));
        Long expectedSizeOfFirstList = 2L;
        assertEquals(expectedSizeOfFirstList, actualList.size());
        for (BookDto expected : expectedList) {
            assertTrue(actualList.stream().anyMatch(e ->
                            EqualsBuilder.reflectionEquals(expected, e, "id", "isbn")),
                    String.format("actual list didn't match expected BookDto: %s", expected));
        }
    }

    @Test
    @DisplayName("Updating a book by a valid ID returns the updated BookDto with Ok status")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateBookById_Valid_ReturnsUpdatedBookDto() throws Exception {
        CreateBookRequestDto requestDto = TestUtil
                .createBookRequestDto("Third Book", "Third Author", 15);
        Long id = 10L;
        requestDto.setPrice(BigDecimal.valueOf(20));
        requestDto.setDescription("new description");
        BookDto expectedUpdatedBookDto = TestUtil.createBookDtoFromRequest(requestDto);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put(BOOKS_BY_ID_API_URL, id)
                        .contentType(APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        assertTrue(EqualsBuilder.reflectionEquals(expectedUpdatedBookDto, actual, "id", "isbn"),
                String.format("expected BookDto: %s, but got %s", expectedUpdatedBookDto, actual));
        assertEquals(BigDecimal.valueOf(20), actual.getPrice());
        assertEquals("new description", actual.getDescription());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Adding a book to a category, controller returns updated BookDto")
    void addBookToCategory_ValidIds_ReturnsUpdateBookDto() throws Exception {
        CreateBookRequestDto request = TestUtil.createBookRequestDto(
                "Second Book", "Second Author", 30);
        BookDto bookDto = TestUtil.createBookDtoFromRequest(request);
        Long bookId = 101L;
        Long categoryId = 2L;

        MvcResult result = mockMvc.perform(post(ADD_BOOK_TO_CATEGORY_API_URL,
                        bookId, categoryId))
                .andExpect(status().isOk())
                .andReturn();

        BookDto updatedBookDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        assertTrue(EqualsBuilder.reflectionEquals(
                bookDto, updatedBookDto, "categoryIds", "id", "isbn"),
                String.format("expected BookDto: %s, but got %s", bookDto, updatedBookDto));
        assertNotNull(updatedBookDto.getCategoryIds());
        assertTrue(updatedBookDto.getCategoryIds().contains(2L),
                "The updated BookDto does not contain the ID of the added category (2)");
    }
}

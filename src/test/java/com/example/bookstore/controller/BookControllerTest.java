package com.example.bookstore.controller;

import com.example.bookstore.config.CustomMySqlContainer;
import com.example.bookstore.dto.book.BookDto;
import com.example.bookstore.dto.book.CreateBookRequestDto;
import com.example.bookstore.dto.category.CategoryDto;
import com.example.bookstore.dto.category.CategoryRequestDto;
import com.example.bookstore.service.BookService;
import com.example.bookstore.service.CategoryService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final BookService bookService;
    private final CategoryService categoryService;
    private static final CustomMySqlContainer container = CustomMySqlContainer.getInstance();
    private static final String BOOKS_API_URL = "/api/books";
    private static final String BOOKS_BY_ID_API_URL = "/api/books/{id}";
    private static final String BOOKS_SEARCH_API_URL = "/api/books/search";
    private static final String ADD_BOOK_TO_CATEGORY_API_URL =
            "/api/books/{bookId}/categories/{categoryId}";
    private static final int GET_ALL_RECORDS = Integer.MAX_VALUE;

    @BeforeEach
    void setUp() {
        clearBooksDataBase();
    }

    @Autowired
    public BookControllerTest(MockMvc mockMvc, ObjectMapper objectMapper, BookService bookService, CategoryService categoryService) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.bookService = bookService;
        this.categoryService = categoryService;
    }

    @BeforeAll
    static void beforeAll() {
        container.start();
    }

    private CreateBookRequestDto createRequestDto(int price, String author) {
        String uniqueIsbn = UUID.randomUUID().toString();
        return CreateBookRequestDto.builder()
                .author(author)
                .title("Effective Java 3")
                .isbn(uniqueIsbn)
                .description("best practices for Java Platform")
                .price(BigDecimal.valueOf(price))
                .coverImage("some image")
                .isDeleted(false)
                .build();
    }

    private void clearBooksDataBase() {
        Pageable pageable = PageRequest.of(0, GET_ALL_RECORDS);
        List<BookDto> all = bookService.findAllWithCategories();
        List<Long> existingIds = all.stream().map(BookDto::getId).toList();
        for (Long id : existingIds) {
            bookService.deleteById(id);
        }
    }

    private BookDto createBookFromDto(CreateBookRequestDto requestDto) {
        BookDto savedBook = bookService.save(requestDto);
        savedBook.setPrice(savedBook.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP));
        return savedBook;
    }

    @Test
    @DisplayName("Creating a new book with valid data returns BookDto with Created status")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createNewBook_Valid_ReturnsBookDto() throws Exception {
        CreateBookRequestDto requestDto = createRequestDto(25, "Author");

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

        BookDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), BookDto.class);
        assertNotNull(actual.getId());

        assertNotNull(actual.getId());
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @Test
    @DisplayName("Getting a book by a valid ID returns BookDto with Ok status")
    @WithMockUser(username = "user")
    void getBookById_ValidId_ReturnsDto() throws Exception {
        CreateBookRequestDto requestDto = createRequestDto(25, "Author");
        BookDto expected = createBookFromDto(requestDto);
        Long id = expected.getId();

        MvcResult result = mockMvc.perform(get(BOOKS_BY_ID_API_URL, id))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), BookDto.class);

        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("Getting all books returns a list of BookDto with Ok status")
    @WithMockUser(username = "user")
    void getAllBooks_Valid_ReturnsListOfBookDto() throws Exception {
        CreateBookRequestDto requestDto = createRequestDto(25, "Author");
        BookDto expectedBook = createBookFromDto(requestDto);

        CreateBookRequestDto requestDto2 = createRequestDto(30, "Author");
        BookDto expectedDto2 = createBookFromDto(requestDto2);

        List<BookDto> expected = List.of(expectedBook, expectedDto2);
        Pageable pageable = Pageable.ofSize(10);

        MvcResult result = mockMvc.perform(get(BOOKS_API_URL)
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize())))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDto> actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), new TypeReference<List<BookDto>>() {
                });

        Assertions.assertEquals(expected.size(), actual.size());
        Assertions.assertTrue(actual.stream().anyMatch(dto -> EqualsBuilder.reflectionEquals(dto, expectedBook)));
        Assertions.assertTrue(actual.stream().anyMatch(dto -> EqualsBuilder.reflectionEquals(dto, expectedDto2)));
    }

    @Test
    @DisplayName("Deleting a book by a valid ID returns NoContent status and the book is not found")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteBook_ValidID() throws Exception {
        CreateBookRequestDto requestDto = createRequestDto(25, "Author");
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

        List<BookDto> actualAllBooks = objectMapper
                .readValue(getAllResult.getResponse().getContentAsString(), new TypeReference<List<BookDto>>() {
                });

        assertTrue(actualAllBooks.stream().noneMatch(book -> book.getId().equals(idToDelete)),
                "Deleted book should not be present in the list of all books.");
    }

    @Test
    @DisplayName("Searching books by valid parameters returns a list of matching BookDto with Ok status")
    @WithMockUser(username = "user")
    void searchBookByParams_Valid() throws Exception {
        CreateBookRequestDto requestDto = createRequestDto(11, "Author1");
        CreateBookRequestDto requestDto2 = createRequestDto(21, "Author2");
        BookDto book = createBookFromDto(requestDto);
        BookDto book2 = createBookFromDto(requestDto2);
        BigDecimal minPrice = BigDecimal.valueOf(10);
        BigDecimal maxPrice = BigDecimal.valueOf(20);

        MvcResult result = mockMvc.perform(get(BOOKS_SEARCH_API_URL)
                        .param("minPrice", minPrice.toString())
                        .param("maxPrice", maxPrice.toString()))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result2 = mockMvc.perform(get(BOOKS_SEARCH_API_URL)
                        .param("authors", requestDto2.getAuthor()))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDto> actualList = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<BookDto>>() {
                });

        actualList.stream().forEach(dto -> dto.setPrice(dto.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP)));

        List<BookDto> actualList2 = objectMapper.readValue(result2.getResponse().getContentAsString(),
                new TypeReference<List<BookDto>>() {
                });
        actualList2.stream().forEach(dto -> dto.setPrice(dto.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP)));

        Assertions.assertEquals(1L, actualList.size());
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(book, actualList.get(0)));

        Assertions.assertEquals(1L, actualList2.size());
        Assertions.assertEquals(book2.getAuthor(), actualList2.get(0).getAuthor(), "Authors should match");
        Assertions.assertEquals(0, book2.getPrice().compareTo(actualList2.get(0).getPrice()), "Prices should match");
        Assertions.assertEquals(book2.getTitle(), actualList2.get(0).getTitle(), "Titles should match");
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(book2, actualList2.get(0)));
    }

    @Test
    @DisplayName("Updating a book by a valid ID returns the updated BookDto with Ok status")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateBookById_Valid_ReturnsUpdatedBookDto() throws Exception {
        CreateBookRequestDto requestDto = createRequestDto(25, "Author");
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


        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expectedUpdatedBookDto, actual));
        Assertions.assertEquals(BigDecimal.valueOf(15), actual.getPrice());
        Assertions.assertEquals("new description", actual.getDescription());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Adding a book to a category, controller returns updated BookDto")
    void addBookToCategory_ValidIds_ReturnsUpdateBookDto() throws Exception {
        CreateBookRequestDto request = createRequestDto(25, "Incognito");
        BookDto bookDto = createBookFromDto(request);

        CategoryRequestDto dto = new CategoryRequestDto();
        dto.setName("Adventure");
        dto.setDescription("some description");
        CategoryDto categoryDto = categoryService.save(dto);

        MvcResult result = mockMvc.perform(post(ADD_BOOK_TO_CATEGORY_API_URL, bookDto.getId(),
                        categoryDto.getId()))
                .andExpect(status().isOk())
                .andReturn();

        BookDto updatedBookDto = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookDto.class);

        Assertions.assertTrue(EqualsBuilder.reflectionEquals(bookDto, updatedBookDto, "categoryIds"));
        Assertions.assertNotNull(updatedBookDto.getCategoryIds());
        Assertions.assertTrue(updatedBookDto.getCategoryIds().contains(categoryDto.getId()),
                "Book must contain the added category id");
    }
}

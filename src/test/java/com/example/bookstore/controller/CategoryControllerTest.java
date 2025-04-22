package com.example.bookstore.controller;

import com.example.bookstore.config.CustomMySqlContainer;
import com.example.bookstore.dto.book.BookDto;
import com.example.bookstore.dto.book.BookDtoWithoutCategoryIds;
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
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final CategoryService categoryService;
    private final BookService bookService;
    private static final CustomMySqlContainer container = CustomMySqlContainer.getInstance();
    private static final String CATEGORIES_API_URL = "/api/categories";
    private static final String CATEGORY_BY_ID_API_URL = "/api/categories/{id}";
    private static final String CATEGORY_BOOKS_API_URL = "/api/categories/{id}/books";

    @Autowired
    public CategoryControllerTest(MockMvc mockMvc, ObjectMapper objectMapper, CategoryService categoryService, BookService bookService) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.categoryService = categoryService;
        this.bookService = bookService;
    }

    @BeforeAll
    static void beforeAll() {
        container.start();
    }

    @BeforeEach
    void setUp() {
        clearCategoryDataBase();
    }

    private void clearCategoryDataBase() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        List<CategoryDto> all = categoryService.findAll(pageable);
        List<Long> existingIds = all.stream().map(CategoryDto::getId).toList();
        for (Long id : existingIds) {
            categoryService.deleteById(id);
        }
    }

    private CategoryRequestDto createCategoryRequestDto(String name){
        CategoryRequestDto dto = new CategoryRequestDto();
        dto.setName(name);
        dto.setDescription("some description");
        return dto;
    }

    private CategoryDto saveCategory(CategoryRequestDto dto) {
        return categoryService.save(dto);
    }

    @Test
    @DisplayName("Creating a valid category returns CategoryDto with Created status")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createCategory_ValidReturnsCategoryDto() throws Exception {
        CategoryRequestDto requestDto = createCategoryRequestDto("Roman");
        CategoryDto expectedCategory = new CategoryDto();
        expectedCategory.setName(requestDto.getName());
        expectedCategory.setDescription(requestDto.getDescription());
        expectedCategory.setName(requestDto.getName());
        expectedCategory.setDeleted(false);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(post(CATEGORIES_API_URL)
                        .contentType(APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), CategoryDto.class);
        Assertions.assertNotNull(actual.getId());
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expectedCategory, actual, "id"));
    }

    @Test
    @DisplayName("Getting all categories returns a list of CategoryDto with Ok status")
    @WithMockUser(username = "user")
    void getAllCategories_Valid_ReturnsListOfCategoryDto() throws Exception {
        CategoryRequestDto requestDto = createCategoryRequestDto("Fantasy");
        CategoryRequestDto requestDto2 = createCategoryRequestDto("Roman");
        CategoryRequestDto requestDto3 = createCategoryRequestDto("Adventure");
        CategoryDto dto = saveCategory(requestDto);
        CategoryDto dto2 = saveCategory(requestDto2);
        CategoryDto dto3 = saveCategory(requestDto3);
        List<CategoryDto> expectedList = List.of(dto, dto2, dto3);

        MvcResult result = mockMvc.perform(get(CATEGORIES_API_URL))
                .andExpect(status().isOk())
                .andReturn();

        List<CategoryDto> actualList = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<CategoryDto>>() {
        });

        Assertions.assertEquals(expectedList.size(), actualList.size());
        for (CategoryDto expected : expectedList) {
            Assertions.assertTrue(
                    actualList.stream().anyMatch(actual -> EqualsBuilder.reflectionEquals(actual, expected)),
                    "Expected category not found: " + expected.getName()
            );
        }
    }

    @Test
    @DisplayName("Getting a category by a valid ID returns CategoryDto with Ok status")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getCategoryById_Valid_ReturnsCategoryDto() throws Exception {
        CategoryRequestDto requestDto = createCategoryRequestDto("Special");
        CategoryDto expectedDto = saveCategory(requestDto);
        Long id = expectedDto.getId();

        MvcResult result = mockMvc.perform(get(CATEGORY_BY_ID_API_URL, id))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), CategoryDto.class);

        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expectedDto, actual));
    }

    @Test
    @DisplayName("Updating a valid category by ID returns the updated CategoryDto with Ok status")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateCategoryById_Valid_ReturnsUpdatedCategoryDto() throws Exception {
        CategoryRequestDto requestDto = createCategoryRequestDto("Roman");
        CategoryDto dto = saveCategory(requestDto);
        Long id = dto.getId();
        requestDto.setName("Fantasy-Roman");

        CategoryDto expectedDto = new CategoryDto();
        expectedDto.setName("Fantasy-Roman");
        expectedDto.setDescription(dto.getDescription());
        expectedDto.setId(dto.getId());

        String updatedRequestJson = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put(CATEGORY_BY_ID_API_URL, id)
                        .contentType(APPLICATION_JSON)
                        .content(updatedRequestJson))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), CategoryDto.class);
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expectedDto, actual));
    }

    @Test
    @DisplayName("Deleting a category by a valid ID returns NoContent status and the category is not found")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteCategoryById_Valid() throws Exception {
        CategoryRequestDto requestDto = createCategoryRequestDto("Fantasy");
        CategoryDto category = saveCategory(requestDto);
        Long id = category.getId();

        mockMvc.perform(delete(CATEGORY_BY_ID_API_URL, id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(CATEGORY_BY_ID_API_URL, id))
                .andExpect(status().isNotFound());

        MvcResult getAllResult = mockMvc.perform(get(CATEGORIES_API_URL))
                .andExpect(status().isOk())
                .andReturn();

        List<CategoryDto> categoryDtoList = objectMapper.readValue(getAllResult.getResponse().getContentAsString(), new TypeReference<List<CategoryDto>>() {});
        Assertions.assertTrue(categoryDtoList.stream().noneMatch(categoryDto -> categoryDto.getId().equals(id)));
    }

    @Test
    @DisplayName("Getting books by a valid category ID returns a list of BookDtoWithoutCategoryIds with Ok status")
    @WithMockUser(username = "user")
    void getBooksByCategoryId() throws Exception {
        CreateBookRequestDto requestDto = CreateBookRequestDto.builder()
                .author("Joshua Bloch")
                .title("Effective Java 3")
                .isbn("1234")
                .description("best practices for Java Platform")
                .price(BigDecimal.valueOf(25))
                .coverImage("some image")
                .build();

        CreateBookRequestDto requestDto2 = CreateBookRequestDto.builder()
                .author("J.R.R. Tolkien")
                .title("The Lord of the Rings")
                .isbn("ISBN-003")
                .description("The epic fantasy")
                .price(BigDecimal.valueOf(30))
                .coverImage("lotr.jpg")
                .build();

        BookDto book = bookService.save(requestDto);
        BookDto book2 = bookService.save(requestDto2);
        Long expectedSizeOfList = 2L;

        CategoryDto categoryDto = saveCategory(createCategoryRequestDto("Temporary"));

        bookService.addBookToCategory(book.getId(), categoryDto.getId());
        bookService.addBookToCategory(book2.getId(), categoryDto.getId());

        MvcResult result = mockMvc.perform(get(CATEGORY_BOOKS_API_URL, categoryDto.getId()))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDtoWithoutCategoryIds> actualList = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<BookDtoWithoutCategoryIds>>() {
        });

        Assertions.assertEquals(expectedSizeOfList, actualList.size());
        Assertions.assertEquals(book.getAuthor(), actualList.get(0).getAuthor());
        Assertions.assertEquals(book2.getAuthor(), actualList.get(1).getAuthor());
        Assertions.assertEquals(book.getTitle(), actualList.get(0).getTitle());
        Assertions.assertEquals(book2.getTitle(), actualList.get(1).getTitle());
    }
}

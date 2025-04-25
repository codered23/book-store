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
import com.example.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookstore.dto.book.CreateBookRequestDto;
import com.example.bookstore.dto.category.CategoryDto;
import com.example.bookstore.dto.category.CategoryRequestDto;
import com.example.bookstore.service.BookService;
import com.example.bookstore.service.CategoryService;
import com.example.bookstore.util.TestUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/clean-up.sql")
public class CategoryControllerTest {
    private static final CustomMySqlContainer container = CustomMySqlContainer.getInstance();
    private static final String CATEGORIES_API_URL = "/api/categories";
    private static final String CATEGORY_BY_ID_API_URL = "/api/categories/{id}";
    private static final String CATEGORY_BOOKS_API_URL = "/api/categories/{id}/books";

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final CategoryService categoryService;
    private final BookService bookService;

    @Autowired
    public CategoryControllerTest(MockMvc mockMvc, ObjectMapper objectMapper,
                                  CategoryService categoryService, BookService bookService) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.categoryService = categoryService;
        this.bookService = bookService;
    }

    @BeforeAll
    static void beforeAll() {
        container.start();
    }

    @Test
    @DisplayName("Creating a valid category returns CategoryDto with Created status")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createCategory_ValidReturnsCategoryDto() throws Exception {
        CategoryRequestDto requestDto = TestUtil.createCategoryRequestDto("Roman");
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

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);
        assertNotNull(actual.getId());
        assertTrue(EqualsBuilder.reflectionEquals(expectedCategory, actual, "id"));
    }

    @Test
    @DisplayName("Getting all categories returns a list of CategoryDto with Ok status")
    @WithMockUser(username = "user")
    void getAllCategories_Valid_ReturnsListOfCategoryDto() throws Exception {
        CategoryRequestDto firstRequest = TestUtil.createCategoryRequestDto("Fantasy");
        CategoryRequestDto secondRequest = TestUtil.createCategoryRequestDto("Roman");
        CategoryRequestDto thirdRequest = TestUtil.createCategoryRequestDto("Adventure");
        CategoryDto firstDto = categoryService.save(firstRequest);
        CategoryDto secondDto = categoryService.save(secondRequest);
        CategoryDto thirdDto = categoryService.save(thirdRequest);
        List<CategoryDto> expectedList = List.of(firstDto, secondDto, thirdDto);

        MvcResult result = mockMvc.perform(get(CATEGORIES_API_URL))
                .andExpect(status().isOk())
                .andReturn();

        List<CategoryDto> actualList = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<CategoryDto>>() {});

        assertEquals(expectedList.size(), actualList.size());
        for (CategoryDto expected : expectedList) {
            assertTrue(
                    actualList.stream().anyMatch(
                            actual -> EqualsBuilder.reflectionEquals(actual, expected)),
                    "Expected category not found: " + expected.getName()
            );
        }
    }

    @Test
    @DisplayName("Getting a category by a valid ID returns CategoryDto with Ok status")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getCategoryById_Valid_ReturnsCategoryDto() throws Exception {
        CategoryRequestDto requestDto = TestUtil.createCategoryRequestDto("Special");
        CategoryDto expectedDto = categoryService.save(requestDto);
        Long id = expectedDto.getId();

        MvcResult result = mockMvc.perform(get(CATEGORY_BY_ID_API_URL, id))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);

        assertTrue(EqualsBuilder.reflectionEquals(expectedDto, actual));
    }

    @Test
    @DisplayName("Updating a valid category by ID returns the updated CategoryDto with Ok status")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateCategoryById_Valid_ReturnsUpdatedCategoryDto() throws Exception {
        CategoryRequestDto requestDto = TestUtil.createCategoryRequestDto("Roman");
        CategoryDto dto = categoryService.save(requestDto);
        requestDto.setName("Roman-Adventure");
        dto.setName("Roman-Adventure");

        String updatedRequestJson = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put(CATEGORY_BY_ID_API_URL, dto.getId())
                        .contentType(APPLICATION_JSON)
                        .content(updatedRequestJson))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);
        assertTrue(EqualsBuilder.reflectionEquals(dto, actual));
    }

    @Test
    @DisplayName("Deleting a category by a valid ID returns "
            + "NoContent status and the category is not found")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteCategoryById_Valid() throws Exception {
        CategoryRequestDto requestDto = TestUtil.createCategoryRequestDto("Fantasy");
        CategoryDto category = categoryService.save(requestDto);
        Long id = category.getId();

        mockMvc.perform(delete(CATEGORY_BY_ID_API_URL, id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(CATEGORY_BY_ID_API_URL, id))
                .andExpect(status().isNotFound());

        MvcResult getAllResult = mockMvc.perform(get(CATEGORIES_API_URL))
                .andExpect(status().isOk())
                .andReturn();

        List<CategoryDto> categoryDtoList = objectMapper.readValue(
                getAllResult.getResponse().getContentAsString(),
                new TypeReference<List<CategoryDto>>() {});
        assertTrue(categoryDtoList.stream().noneMatch(
                categoryDto -> categoryDto.getId().equals(id)));
    }

    @Test
    @DisplayName("Getting books by a valid category ID returns "
            + "a list of BookDtoWithoutCategoryIds with Ok status")
    @WithMockUser(username = "user")
    void getBooksByCategoryId() throws Exception {

        CreateBookRequestDto firstRequestDto = TestUtil
                .createBookRequestDto("Effective Java 3", "Joshua Bloch", 25);
        CreateBookRequestDto secondRequestDto = TestUtil
                .createBookRequestDto("Design patterns", "Richard Henrich", 30);

        BookDto firstBook = bookService.save(firstRequestDto);
        BookDto secondBook = bookService.save(secondRequestDto);
        Long expectedSizeOfList = 2L;

        CategoryDto categoryDto = categoryService.save(TestUtil
                .createCategoryRequestDto("Temporary"));

        bookService.addBookToCategory(firstBook.getId(), categoryDto.getId());
        bookService.addBookToCategory(secondBook.getId(), categoryDto.getId());

        MvcResult result = mockMvc.perform(get(CATEGORY_BOOKS_API_URL, categoryDto.getId()))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDtoWithoutCategoryIds> actualList = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<BookDtoWithoutCategoryIds>>() {});

        assertEquals(expectedSizeOfList, actualList.size());
        assertEquals(firstBook.getAuthor(), actualList.get(0).getAuthor());
        assertEquals(secondBook.getAuthor(), actualList.get(1).getAuthor());
        assertEquals(firstBook.getTitle(), actualList.get(0).getTitle());
        assertEquals(secondBook.getTitle(), actualList.get(1).getTitle());
    }
}

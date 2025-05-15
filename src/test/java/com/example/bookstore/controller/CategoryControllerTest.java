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
import com.example.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookstore.dto.category.CategoryDto;
import com.example.bookstore.dto.category.CategoryRequestDto;
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
@Sql(scripts = {"/sql/clean-up.sql",
        "/sql/create-default-categories.sql",
        "/sql/create-default-books.sql",
        "/sql/add-default-book-categories.sql"})
@Sql(scripts = "/sql/clean-up.sql", executionPhase = AFTER_TEST_METHOD)
public class CategoryControllerTest {
    private static final CustomMySqlContainer container = CustomMySqlContainer.getInstance();
    private static final String CATEGORIES_API_URL = "/api/categories";
    private static final String CATEGORY_BY_ID_API_URL = "/api/categories/{id}";
    private static final String CATEGORY_BOOKS_API_URL = "/api/categories/{id}/books";

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public CategoryControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
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
        CategoryDto expectedDto = TestUtil.createCategoryDtoFromRequest(requestDto);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(post(CATEGORIES_API_URL)
                        .contentType(APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);
        assertNotNull(actual.getId());
        assertTrue(EqualsBuilder.reflectionEquals(expectedDto, actual, "id"),
                String.format("Expected created category: %s, but got: %s", expectedDto, actual));
    }

    @Test
    @DisplayName("Getting all categories returns a list of CategoryDto with Ok status")
    @WithMockUser(username = "user")
    void getAllCategories_Valid_ReturnsListOfCategoryDto() throws Exception {
        List<CategoryDto> expectedList = TestUtil.getAllCategoryDto();

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
                            actual -> EqualsBuilder.reflectionEquals(actual, expected, "id")),
                    String.format("Expected category not found: %s", expected)
            );
        }
    }

    @Test
    @DisplayName("Getting a category by a valid ID returns CategoryDto with Ok status")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getCategoryById_Valid_ReturnsCategoryDto() throws Exception {
        CategoryRequestDto requestDto = TestUtil.createCategoryRequestDto("Adventure");
        CategoryDto expectedDto = TestUtil.createCategoryDtoFromRequest(requestDto);
        Long categoryId = 2L;

        MvcResult result = mockMvc.perform(get(CATEGORY_BY_ID_API_URL, categoryId))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);

        assertTrue(EqualsBuilder.reflectionEquals(expectedDto, actual, "id"),
                String.format("Expected created category: %s, but got: %s", expectedDto, actual));
    }

    @Test
    @DisplayName("Getting category by non-existent ID returns 404 Not Found")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getCategoryById_NonExistentId_ReturnsNotFound() throws Exception {
        Long nonExistentId = 99999L;

        MvcResult result = mockMvc.perform(get(CATEGORY_BY_ID_API_URL, nonExistentId))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @DisplayName("Updating a valid category by ID returns the updated CategoryDto with Ok status")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateCategoryById_Valid_ReturnsUpdatedCategoryDto() throws Exception {
        CategoryRequestDto requestDto = TestUtil.createCategoryRequestDto("Roman-Adventure");
        requestDto.setDescription("involves supernatural or magical elements");
        CategoryDto expectedDto = TestUtil.createCategoryDtoFromRequest(requestDto);

        String updatedRequestJson = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put(CATEGORY_BY_ID_API_URL, 1L)
                        .contentType(APPLICATION_JSON)
                        .content(updatedRequestJson))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);
        assertTrue(EqualsBuilder.reflectionEquals(expectedDto, actual, "id"),
                String.format("Expected updated category: %s, but got: %s", expectedDto, actual));
    }

    @Test
    @DisplayName("Deleting a category by a valid ID returns "
            + "NoContent status and the category is not found")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteCategoryById_Valid() throws Exception {
        Long categoryId = 2L;
        mockMvc.perform(get(CATEGORY_BY_ID_API_URL, categoryId))
                .andExpect(status().isOk());

        mockMvc.perform(delete(CATEGORY_BY_ID_API_URL, categoryId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(CATEGORY_BY_ID_API_URL, categoryId))
                .andExpect(status().isNotFound());

        MvcResult getAllResult = mockMvc.perform(get(CATEGORIES_API_URL))
                .andExpect(status().isOk())
                .andReturn();

        List<CategoryDto> categoryDtoList = objectMapper.readValue(
                getAllResult.getResponse().getContentAsString(),
                new TypeReference<List<CategoryDto>>() {});
        assertTrue(categoryDtoList.stream().noneMatch(
                categoryDto -> categoryDto.getId().equals(categoryId)),
                String.format("Expected category still exist in return list: categoryId: %s",
                        categoryId));
    }

    @Test
    @DisplayName("Getting books by a valid category ID returns "
            + "a list of BookDtoWithoutCategoryIds with Ok status")
    @WithMockUser(username = "user")
    void getBooksByCategoryId() throws Exception {
        List<BookDtoWithoutCategoryIds> expectedList = TestUtil.getAllBookDtoWithoutCategoryIds();
        Long categoryId = 3L;

        MvcResult result = mockMvc.perform(get(CATEGORY_BOOKS_API_URL, categoryId))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDtoWithoutCategoryIds> actualList = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<BookDtoWithoutCategoryIds>>() {});

        assertEquals(expectedList.size(), actualList.size());
        for (BookDtoWithoutCategoryIds expectedBook : expectedList) {
            assertTrue(actualList.stream().anyMatch(e -> EqualsBuilder.reflectionEquals(
                    e, expectedBook, "id", "isbn")),
                    String.format("Expected category is not found: %s", expectedBook));
        }
    }
}

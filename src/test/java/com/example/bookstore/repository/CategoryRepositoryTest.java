package com.example.bookstore.repository;

import com.example.bookstore.config.CustomMySqlContainer;
import com.example.bookstore.model.Category;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;
    private Category romanCategory;
    private static final CustomMySqlContainer container = CustomMySqlContainer.getInstance();

    @BeforeAll
    static void beforeAll() {
        container.start();
    }

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();

        romanCategory = new Category();
        romanCategory.setName("Roman");
        romanCategory.setDescription("the story about 3 friends");
        categoryRepository.save(romanCategory);
    }

    @Test
    @DisplayName("Finding all categories with pagination returns the correct page")
    void findAll_Valid_returnsPageOfCategory() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Category> all = categoryRepository.findAll(pageable);

        int actualTotalPages = all.getTotalPages();
        long actualCountOfCategories = all.getTotalElements();
        Assertions.assertEquals(1, actualTotalPages);
        Assertions.assertEquals(1L, actualCountOfCategories);
        Assertions.assertTrue(all.getContent().contains(romanCategory));
    }
}

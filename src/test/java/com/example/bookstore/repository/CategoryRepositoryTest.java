package com.example.bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.bookstore.config.CustomMySqlContainer;
import com.example.bookstore.model.Category;
import com.example.bookstore.util.TestUtil;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"/sql/clean-up.sql",
        "/sql/create-default-categories.sql"})
public class CategoryRepositoryTest {
    private static final CustomMySqlContainer container = CustomMySqlContainer.getInstance();
    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeAll
    static void beforeAll() {
        container.start();
    }

    @Test
    @DisplayName("Finding all categories with pagination returns the correct page")
    void findAll_Valid_returnsPageOfCategory() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Category> expectedList = TestUtil.getAllCategory();

        Page<Category> all = categoryRepository.findAll(pageable);

        int actualTotalPages = all.getTotalPages();
        long actualCountOfCategories = all.getTotalElements();
        assertEquals(1, actualTotalPages);
        assertEquals(3L, actualCountOfCategories);
        for (Category expected : expectedList) {
            assertTrue(all.getContent().stream().anyMatch(e ->
                            EqualsBuilder.reflectionEquals(e, expected, "id")),
                    String.format(
                            "Expected category is not found in actual list: %s", expected));
        }
    }
}

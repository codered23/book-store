package com.example.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.bookstore.dto.category.CategoryDto;
import com.example.bookstore.dto.category.CategoryRequestDto;
import com.example.bookstore.mapper.CategoryMapper;
import com.example.bookstore.model.Category;
import com.example.bookstore.repository.CategoryRepository;
import com.example.bookstore.service.impl.CategoryServiceImpl;
import com.example.bookstore.util.TestUtil;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;
    private Category category;
    private CategoryDto categoryDto;
    private CategoryRequestDto categoryRequestDto;

    @BeforeEach
    void setUp() {
        categoryRequestDto = TestUtil.createCategoryRequestDto("Roman");
        category = TestUtil.createCategory(categoryRequestDto);
        categoryDto = TestUtil.createCategoryDto(category);
    }

    @Test
    @DisplayName("Verify save() method works")
    void save_ValidCategoryRequestDto_ReturnsCategoryDto() {
        when(categoryMapper.toModel(categoryRequestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto save = categoryService.save(categoryRequestDto);

        assertTrue(EqualsBuilder.reflectionEquals(save, categoryDto));
        verify(categoryRepository).save(category);
        verify(categoryMapper).toModel(categoryRequestDto);
        verify(categoryMapper).toDto(category);
        verifyNoMoreInteractions(categoryMapper, categoryRepository);
    }

    @Test
    @DisplayName("Find category by valid ID")
    void getById_ValidIdReturnsCategoryDto() {
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto actual = categoryService.findById(category.getId());

        assertTrue(EqualsBuilder.reflectionEquals(categoryDto, actual));
        verify(categoryRepository).findById(category.getId());
        verify(categoryMapper).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Find all categories with valid pageable and return a list of DTOs")
    void findAll_ValidPageable_ReturnsListDto() {
        Pageable pageable = PageRequest.of(1, 10);
        List<Category> categories = List.of(category);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        List<CategoryDto> actual = categoryService.findAll(pageable);

        assertEquals(1, actual.size());
        assertTrue(actual.stream().anyMatch(
                dto -> EqualsBuilder.reflectionEquals(dto, categoryDto)));
        verify(categoryRepository).findAll(pageable);
        verify(categoryMapper).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Update category with valid ID and return updated DTO")
    void update_Valid_ReturnsDto() {
        category.setDescription("new description");
        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryMapper.toModel(categoryRequestDto)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);
        when(categoryRepository.save(categoryCaptor.capture())).thenReturn(category);

        CategoryDto actual = categoryService.update(category.getId(), categoryRequestDto);
        Category saved = categoryCaptor.getValue();

        assertTrue(EqualsBuilder.reflectionEquals(actual, categoryDto));
        assertEquals(saved.getDescription(), "new description");
        verify(categoryRepository).save(category);
        verify(categoryRepository).findById(category.getId());
        verify(categoryMapper).toModel(categoryRequestDto);
        verify(categoryMapper).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Delete category by valid ID")
    void deleteById_Valid_CallsRepositoryDeleteMethod() {
        Long idToDelete = 123L;
        categoryService.deleteById(idToDelete);

        verify(categoryRepository).deleteById(idToDelete);
        verifyNoMoreInteractions(categoryRepository);
    }
}

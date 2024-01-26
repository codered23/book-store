package com.example.bookstore.service;

import com.example.bookstore.dto.CategoryDto;
import com.example.bookstore.dto.CategoryRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto getById(Long id);

    CategoryDto save(CategoryRequestDto categoryRequestDto);

    CategoryDto update(Long id, CategoryRequestDto categoryRequestDto);

    void deleteById(Long id);
}

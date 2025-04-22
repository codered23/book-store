package com.example.bookstore.service.impl;

import com.example.bookstore.dto.category.CategoryDto;
import com.example.bookstore.dto.category.CategoryRequestDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.CategoryMapper;
import com.example.bookstore.model.Category;
import com.example.bookstore.repository.CategoryRepository;
import com.example.bookstore.service.CategoryService;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        return repository.findAll(pageable).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto findById(Long id) {
        return mapper.toDto(repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Category not found with id: " + id)));
    }

    @Override
    public CategoryDto save(CategoryRequestDto categoryRequestDto) {
        return mapper.toDto(repository.save(mapper.toModel(categoryRequestDto)));
    }

    @Override
    public CategoryDto update(Long id, CategoryRequestDto categoryRequestDto) {
        repository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Category not found with id: " + id));
        Category model = mapper.toModel(categoryRequestDto);
        model.setId(id);
        return mapper.toDto(repository.save(model));
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}

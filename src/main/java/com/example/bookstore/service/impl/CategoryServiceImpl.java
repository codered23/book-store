package com.example.bookstore.service.impl;

import com.example.bookstore.dto.category.CategoryDto;
import com.example.bookstore.dto.category.CategoryRequestDto;
import com.example.bookstore.mapper.CategoryMapper;
import com.example.bookstore.model.Category;
import com.example.bookstore.repository.category.CategoryRepository;
import com.example.bookstore.service.CategoryService;
import java.util.List;
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
    public CategoryDto getById(Long id) {
        return mapper.toDto(repository.getById(id));
    }

    @Override
    public CategoryDto save(CategoryRequestDto categoryRequestDto) {
        return mapper.toDto(repository.save(mapper.toModel(categoryRequestDto)));
    }

    @Override
    public CategoryDto update(Long id, CategoryRequestDto categoryRequestDto) {
        Category model = mapper.toModel(categoryRequestDto);
        model.setId(id);
        return mapper.toDto(repository.save(model));
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}

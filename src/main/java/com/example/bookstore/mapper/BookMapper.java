package com.example.bookstore.mapper;

import com.example.bookstore.config.MapperConfig;
import com.example.bookstore.dto.book.BookDto;
import com.example.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookstore.dto.book.CreateBookRequestDto;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.Category;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = {UserMapper.class})
public interface BookMapper {
    @Mapping(target = "categoryIds", ignore = true)
    BookDto toDto(Book book);

    @AfterMapping
    default void setCategoriesIds(@MappingTarget BookDto dto, Book book) {
        if (!book.getCategories().isEmpty()) {
            Set<Long> repliesId = book.getCategories()
                    .stream()
                    .map(Category::getId)
                    .collect(Collectors.toSet());
            dto.setCategoryIds(repliesId);
        }
    }

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    Book toModel(CreateBookRequestDto requestDto);
}

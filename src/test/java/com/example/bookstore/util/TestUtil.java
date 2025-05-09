package com.example.bookstore.util;

import com.example.bookstore.dto.book.BookDto;
import com.example.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookstore.dto.book.CreateBookRequestDto;
import com.example.bookstore.dto.category.CategoryDto;
import com.example.bookstore.dto.category.CategoryRequestDto;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.Category;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class TestUtil {

    public static Book createBook(CreateBookRequestDto requestDto) {
        return Book.builder()
                .author(requestDto.getAuthor())
                .title(requestDto.getTitle())
                .isbn(requestDto.getIsbn())
                .description(requestDto.getDescription())
                .price(requestDto.getPrice())
                .coverImage(requestDto.getCoverImage()).build();
    }

    public static CreateBookRequestDto createBookRequestDto(
            String title, String author, int price) {
        return CreateBookRequestDto.builder()
                .author(author)
                .title(title)
                .isbn(UUID.randomUUID().toString())
                .description("best practices for Java Platform")
                .price(BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP))
                .coverImage("some image")
                .build();
    }

    public static BookDto createBookDto(Book book) {
        return BookDto.builder().id(book.getId())
                .author(book.getAuthor())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .description(book.getDescription())
                .price(book.getPrice())
                .coverImage(book.getCoverImage()).build();
    }

    public static BookDto createBookDtoFromRequest(CreateBookRequestDto book) {
        return BookDto.builder().id(1L)
                .author(book.getAuthor())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .description(book.getDescription())
                .price(book.getPrice())
                .coverImage(book.getCoverImage()).build();
    }

    public static BookDtoWithoutCategoryIds createBookDtoWithoutCategoryIdsFromRequest(
            CreateBookRequestDto book) {
        return BookDtoWithoutCategoryIds.builder().id(1L)
                .author(book.getAuthor())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .description(book.getDescription())
                .price(book.getPrice())
                .coverImage(book.getCoverImage()).build();
    }

    public static CategoryRequestDto createCategoryRequestDto(String name) {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName(name);
        requestDto.setDescription("some description");
        return requestDto;
    }

    public static CategoryDto createCategoryDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }

    public static CategoryDto createCategoryDtoFromRequest(CategoryRequestDto category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(1L);
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }

    public static Category createCategory(CategoryRequestDto categoryRequestDto) {
        Category cat = new Category();
        cat.setName(categoryRequestDto.getName());
        cat.setDescription(categoryRequestDto.getDescription());
        return cat;
    }
}

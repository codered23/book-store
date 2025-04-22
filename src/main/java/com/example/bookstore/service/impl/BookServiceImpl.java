package com.example.bookstore.service.impl;

import com.example.bookstore.dto.book.BookDto;
import com.example.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookstore.dto.book.BookSearchParams;
import com.example.bookstore.dto.book.CreateBookRequestDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.BookMapper;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.Category;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CategoryRepository;
import com.example.bookstore.repository.specifications.BookSpecificationBuilder;
import com.example.bookstore.service.BookService;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;
    private final CategoryRepository categoryRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, BookMapper bookMapper,
            BookSpecificationBuilder bookSpecificationBuilder,
                           CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.bookSpecificationBuilder = bookSpecificationBuilder;
        this.categoryRepository = categoryRepository;
    }

    public BookDto save(CreateBookRequestDto product) {
        Book save = bookRepository.save(bookMapper.toModel(product));
        return bookMapper.toDto(save);
    }

    public List<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    public BookDto findById(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Book with id " + id + " not found"));
    }

    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public BookDto update(Long id, CreateBookRequestDto requestDto) {
        bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Book with id " + id + " not found. Please check the book id."));
        Book book = bookMapper.toModel(requestDto);
        book.setId(id);
        return bookMapper.toDto(bookRepository.save(book));
    }

    public List<BookDto> search(BookSearchParams params, Pageable pageable) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(params);
        return bookRepository.findAll(bookSpecification, pageable)
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }

    public List<BookDtoWithoutCategoryIds> findAllByCategoryId(Long id) {
        return bookRepository.findAllByCategoriesId(id).stream()
                .map(bookMapper::toDtoWithoutCategories)
                .toList();
    }

    @Override
    @Transactional
    public BookDto addBookToCategory(Long bookId, Long categoryId) {
        Book byId = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Book with id " + bookId + " not found. Please check the book id."));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Category with id " + categoryId
                                + " not found. Please check the category id."));
        Set<Category> categories = byId.getCategories();
        if (categories == null) {
            HashSet<Category> set = new HashSet<>();
            set.add(category);
            byId.setCategories(set);
        } else {
            categories.add(category);
        }
        bookRepository.save(byId);

        Book refreshedBook = bookRepository.findById(byId.getId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found after save"));

        BookDto dto = bookMapper.toDto(refreshedBook);
        return dto;
    }

    @Override
    public List<BookDto> findAllWithCategories() {
        return bookRepository.findAllWithCategories().stream()
                .map(bookMapper::toDto)
                .toList();
    }
}

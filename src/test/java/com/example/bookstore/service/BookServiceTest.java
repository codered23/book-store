package com.example.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.bookstore.dto.book.BookDto;
import com.example.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookstore.dto.book.BookSearchParams;
import com.example.bookstore.dto.book.CreateBookRequestDto;
import com.example.bookstore.mapper.BookMapper;
import com.example.bookstore.model.Book;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.specifications.BookSpecificationBuilder;
import com.example.bookstore.service.impl.BookServiceImpl;
import com.example.bookstore.util.TestUtil;
import java.math.BigDecimal;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder specificationBuilder;
    @InjectMocks
    private BookServiceImpl bookService;
    private Book book;
    private CreateBookRequestDto requestDto;
    private BookDto bookDto;

    @BeforeEach
    void setUp() {
        requestDto = TestUtil.createBookRequestDto("Effective Java 3", "Joshua Bloch", 25);
        book = TestUtil.createBook(requestDto);
        bookDto = TestUtil.createBookDto(book);
    }

    @Test
    @DisplayName("Verify save() method works")
    void save_ValidCreateRequestDtoReturnsBookDto() {
        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);
        when(bookRepository.save(book)).thenReturn(book);

        BookDto actual = bookService.save(requestDto);

        assertTrue(EqualsBuilder.reflectionEquals(actual, bookDto));
        verify(bookRepository).save(book);
        verify(bookMapper).toDto(book);
        verify(bookMapper).toModel(requestDto);
        verifyNoMoreInteractions(bookMapper, bookRepository);
    }

    @Test
    @DisplayName("Find all books with valid pageable")
    void findAll_ValidPageable_ReturnsAllBooks() {
        Pageable pageable = Pageable.ofSize(10);
        List<Book> books = List.of(book);
        PageImpl<Book> bookPage = new PageImpl<>(books, pageable, books.size());
        List<BookDto> expectedBookDtoList = List.of(bookDto);

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> actual = bookService.findAll(pageable);

        assertEquals(expectedBookDtoList.size(), actual.size());
        for (BookDto expected : expectedBookDtoList) {
            assertTrue(actual.stream().anyMatch(
                    actualDto -> EqualsBuilder.reflectionEquals(actualDto, expected)));
        }
        verify(bookRepository).findAll(pageable);
        verify(bookMapper).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Find book by valid ID")
    void findById_ValidId_ReturnsBookDto() {
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto actual = bookService.findById(book.getId());

        assertTrue(EqualsBuilder.reflectionEquals(bookDto, actual));
        verify(bookRepository).findById(book.getId());
        verify(bookMapper).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Delete book by valid ID")
    void deleteById_Valid_CallsRepositoryDeleteMethod() {
        Long bookIdToDelete = 123L;

        bookService.deleteById(bookIdToDelete);

        verify(bookRepository).deleteById(bookIdToDelete);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Update book with valid ID")
    void update_Valid_ReturnsDto() {
        Book updatedBook = TestUtil.createBook(requestDto);
        updatedBook.setPrice(BigDecimal.valueOf(35));
        updatedBook.setIsbn("0000");
        BookDto bookDto = TestUtil.createBookDto(updatedBook);
        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);
        when(bookMapper.toModel(requestDto)).thenReturn(updatedBook);
        when(bookMapper.toDto(updatedBook)).thenReturn(bookDto);

        BookDto actualBookDto = bookService.update(bookDto.getId(), requestDto);
        verify(bookRepository).save(bookCaptor.capture());
        Book saved = bookCaptor.getValue();

        assertTrue(EqualsBuilder.reflectionEquals(bookDto, actualBookDto));
        assertEquals(saved.getId(), book.getId());
        assertEquals(saved.getPrice(), BigDecimal.valueOf(35));
        assertEquals(saved.getIsbn(), "0000");

        verify(bookRepository).findById(bookDto.getId());
        verify(bookMapper).toDto(updatedBook);
        verify(bookMapper).toModel(requestDto);
        verifyNoMoreInteractions(bookMapper, bookRepository);
    }

    @Test
    @DisplayName("Search books with valid parameters")
    void search_Valid_ReturnsListForSpecification() {
        String[] authors = {"Joshua Bloch"};
        List<Book> bookList = List.of(book);
        BookSearchParams params = new BookSearchParams(authors, null, null);
        Specification<Book> expectedSpecification = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("author"), "Joshua Bloch");
        Pageable pageable = Pageable.ofSize(10);
        Page<Book> bookPage = new PageImpl<>(bookList, pageable, bookList.size());

        when(specificationBuilder.build(params)).thenReturn(expectedSpecification);
        when(bookRepository.findAll(expectedSpecification, pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> actual = bookService.search(params, pageable);

        assertEquals(1, actual.size());
        assertTrue(actual.stream().anyMatch(
                actualDto -> EqualsBuilder.reflectionEquals(bookDto, actualDto)));
        verify(bookRepository).findAll(expectedSpecification, pageable);
        verify(specificationBuilder).build(params);
        verify(bookMapper).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper, specificationBuilder);
    }

    @Test
    @DisplayName("Find all books by category ID returns list of BookDtoWithoutCategoryIds")
    void findAllByCategoryId_Valid_ReturnsListOfBooksDto() {
        BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds = BookDtoWithoutCategoryIds.builder()
                .author("Joshua Bloch")
                .title("Effective Java 3")
                .isbn("1234442")
                .description("best practices for Java Platform")
                .price(BigDecimal.valueOf(75))
                .coverImage("some image")
                .build();
        List<BookDtoWithoutCategoryIds> expectedList = List.of(bookDtoWithoutCategoryIds);
        when(bookRepository.findAllByCategoriesId(book.getId())).thenReturn(List.of(book));
        when(bookMapper.toDtoWithoutCategories(book)).thenReturn(bookDtoWithoutCategoryIds);

        List<BookDtoWithoutCategoryIds> actual = bookService.findAllByCategoryId(book.getId());

        assertEquals(expectedList.size(), actual.size());
        for (BookDtoWithoutCategoryIds expected : expectedList) {
            assertTrue(actual.stream().anyMatch(
                    actualDto -> EqualsBuilder.reflectionEquals(actualDto, expected)));
        }
        verify(bookRepository).findAllByCategoriesId(book.getId());
        verify(bookMapper).toDtoWithoutCategories(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }
}

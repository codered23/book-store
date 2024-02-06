package com.example.bookstore.controller;

import com.example.bookstore.dto.book.BookDto;
import com.example.bookstore.dto.book.BookSearchParams;
import com.example.bookstore.dto.book.CreateBookRequestDto;
import com.example.bookstore.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book-store api", description = "Endpoints for managing books")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/books")
public class BookController {
    private final BookService bookService;

    @GetMapping
    @Operation(summary = "Get all books",
            description = "Get a list of all available books with pagination")
    public ResponseEntity<List<BookDto>> getAll(Pageable pageable) {
        List<BookDto> all = bookService.findAll(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(all);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by id", description = "Get book by id if it exists")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
        BookDto byId = bookService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(byId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    @Operation(summary = "Create a new book", description = "Create a new book by JSON body")
    public ResponseEntity<BookDto> createBook(@RequestBody @Valid CreateBookRequestDto requestDto) {
        BookDto save = bookService.save(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(save);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update book by id", description = "Update book by JSON body")
    public ResponseEntity<BookDto> update(@PathVariable Long id,
                                          @RequestBody @Valid CreateBookRequestDto requestDto) {
        BookDto update = bookService.update(id, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(update);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete book by id", description = "Used soft delete here")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Search book by params",
            description = "Params: minPrice, maxPrice and list of authors")
    public ResponseEntity<List<BookDto>> search(BookSearchParams bookSearchParameters,
                                                Pageable pageable) {
        List<BookDto> search = bookService.search(bookSearchParameters, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(search);
    }
}

package com.example.bookstore.controller;

import com.example.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookstore.dto.category.CategoryDto;
import com.example.bookstore.dto.category.CategoryRequestDto;
import com.example.bookstore.service.BookService;
import com.example.bookstore.service.CategoryService;
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

@Tag(name = "Book-store api", description = "Endpoints for managing categories")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final BookService bookService;

    @GetMapping
    @Operation(summary = "Get all categories",
            description = "Get a list of all available categories")
    public ResponseEntity<List<CategoryDto>> getAll(Pageable pageable) {
        List<CategoryDto> all = categoryService.findAll(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(all);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Get category by id", description = "Get category by id if it exists")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id) {
        CategoryDto byId = categoryService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(byId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    @Operation(summary = "Create a new category",
            description = "Create a new category by JSON body")
    public ResponseEntity<CategoryDto> createCategory(@RequestBody @Valid
                                                          CategoryRequestDto requestDto) {
        CategoryDto save = categoryService.save(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(save);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update category by id",
            description = "Update category by JSON body")
    public ResponseEntity<CategoryDto> update(@PathVariable Long id,
                                              @RequestBody @Valid CategoryRequestDto requestDto) {
        CategoryDto update = categoryService.update(id, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(update);
    }

    @GetMapping("/{id}/books")
    @Operation(summary = "Get all books by category",
            description = "Return list of books by similar category")
    public ResponseEntity<List<BookDtoWithoutCategoryIds>> getBooksByCategoryId(@PathVariable
                                                                                    Long id) {
        List<BookDtoWithoutCategoryIds> allByCategoryId = bookService.findAllByCategoryId(id);
        return ResponseEntity.status(HttpStatus.OK).body(allByCategoryId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category by id", description = "Used soft delete here")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
    }
}

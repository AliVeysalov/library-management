package com.library.controller;

import com.library.dto.book.BookRequest;
import com.library.dto.book.BookResponse;
import com.library.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Kitab idarəetmə API-ları")
public class BookController {

    private final BookService bookService;

    @GetMapping
    @Operation(summary = "Bütün kitabları gətir")
    public ResponseEntity<Page<BookResponse>> getAllBooks(
            @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        return ResponseEntity.ok(bookService.getAllBooks(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "ID-yə görə kitab gətir")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Kitab axtar (ad, müəllif, ISBN)")
    public ResponseEntity<Page<BookResponse>> searchBooks(
            @RequestParam String keyword,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(bookService.searchBooks(keyword, pageable));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Kateqoriyaya görə kitablar")
    public ResponseEntity<Page<BookResponse>> getBooksByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(bookService.getBooksByCategory(categoryId, pageable));
    }

    @GetMapping("/available")
    @Operation(summary = "Mövcud kitablar")
    public ResponseEntity<Page<BookResponse>> getAvailableBooks(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(bookService.getAvailableBooks(pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Yeni kitab əlavə et", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Kitabı yenilə", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request) {
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Kitabı sil (yalnız ADMIN)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}

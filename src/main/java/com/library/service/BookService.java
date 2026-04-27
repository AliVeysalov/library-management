package com.library.service;

import com.library.dto.book.BookRequest;
import com.library.dto.book.BookResponse;
import com.library.entity.Book;
import com.library.entity.Category;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookRepository;
import com.library.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    // Bütün kitabları gətir (səhifəli)
    public Page<BookResponse> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable).map(this::toResponse);
    }

    // ID-yə görə kitab gətir
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kitab tapılmadı: " + id));
        return toResponse(book);
    }

    // Axtarış
    public Page<BookResponse> searchBooks(String keyword, Pageable pageable) {
        return bookRepository.searchBooks(keyword, pageable).map(this::toResponse);
    }

    // Kateqoriyaya görə
    public Page<BookResponse> getBooksByCategory(Long categoryId, Pageable pageable) {
        return bookRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable).map(this::toResponse);
    }

    // Mövcud kitablar
    public Page<BookResponse> getAvailableBooks(Pageable pageable) {
        return bookRepository.findByAvailableStockGreaterThanAndIsActiveTrue(0, pageable).map(this::toResponse);
    }

    // Kitab əlavə et
    @Transactional
    public BookResponse createBook(BookRequest request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new RuntimeException("Bu ISBN artıq mövcuddur: " + request.getIsbn());
        }

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Kateqoriya tapılmadı"));
        }

        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .publisher(request.getPublisher())
                .publishYear(request.getPublishYear())
                .description(request.getDescription())
                .totalStock(request.getTotalStock())
                .availableStock(request.getTotalStock())
                .category(category)
                .build();

        return toResponse(bookRepository.save(book));
    }

    // Kitab yenilə
    @Transactional
    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kitab tapılmadı: " + id));

        // ISBN dəyişibsə, yeni ISBN mövcuddurmu yoxla
        if (!book.getIsbn().equals(request.getIsbn()) && bookRepository.existsByIsbn(request.getIsbn())) {
            throw new RuntimeException("Bu ISBN artıq mövcuddur: " + request.getIsbn());
        }

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Kateqoriya tapılmadı"));
        }

        // Stok fərqini hesabla
        int stockDifference = request.getTotalStock() - book.getTotalStock();

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPublisher(request.getPublisher());
        book.setPublishYear(request.getPublishYear());
        book.setDescription(request.getDescription());
        book.setTotalStock(request.getTotalStock());
        book.setAvailableStock(book.getAvailableStock() + stockDifference);
        book.setCategory(category);

        return toResponse(bookRepository.save(book));
    }

    // Kitab sil (soft delete)
    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kitab tapılmadı: " + id));
        book.setIsActive(false);
        bookRepository.save(book);
    }

    // Entity → DTO
    private BookResponse toResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .publisher(book.getPublisher())
                .publishYear(book.getPublishYear())
                .description(book.getDescription())
                .totalStock(book.getTotalStock())
                .availableStock(book.getAvailableStock())
                .categoryName(book.getCategory() != null ? book.getCategory().getName() : null)
                .createdAt(book.getCreatedAt())
                .build();
    }
}

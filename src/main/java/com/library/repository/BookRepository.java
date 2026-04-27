package com.library.repository;

import com.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    // Ad, müəllif və ya ISBN üzrə axtarış
    @Query("SELECT b FROM Book b WHERE b.isActive = true AND " +
           "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Book> searchBooks(@Param("keyword") String keyword, Pageable pageable);

    // Kateqoriyaya görə axtarış
    Page<Book> findByCategoryIdAndIsActiveTrue(Long categoryId, Pageable pageable);

    // Mövcud kitablar
    Page<Book> findByAvailableStockGreaterThanAndIsActiveTrue(int stock, Pageable pageable);
}

package com.library.repository;

import com.library.entity.BorrowRecord;
import com.library.entity.enums.BorrowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    Page<BorrowRecord> findByUserId(Long userId, Pageable pageable);

    Page<BorrowRecord> findByStatus(BorrowStatus status, Pageable pageable);

    // İstifadəçinin aktiv borrow-larını yoxla
    Optional<BorrowRecord> findByUserIdAndBookIdAndStatus(
            Long userId, Long bookId, BorrowStatus status);

    // Müddəti keçmiş kitablar
    @Query("SELECT br FROM BorrowRecord br WHERE br.status = 'BORROWED' AND br.dueDate < :today")
    List<BorrowRecord> findOverdueRecords(@Param("today") LocalDate today);

    // İstifadəçinin hazırkı kitabları
    List<BorrowRecord> findByUserIdAndStatus(Long userId, BorrowStatus status);

    // Statistika üçün
    long countByStatus(BorrowStatus status);
}

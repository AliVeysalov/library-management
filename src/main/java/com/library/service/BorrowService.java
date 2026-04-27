package com.library.service;

import com.library.dto.borrow.BorrowRequest;
import com.library.dto.borrow.BorrowResponse;
import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.entity.User;
import com.library.entity.enums.BorrowStatus;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRecordRepository;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    // Kitab götür
    @Transactional
    public BorrowResponse borrowBook(Long userId, BorrowRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı"));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Kitab tapılmadı"));

        // Stok yoxlanışı
        if (book.getAvailableStock() <= 0) {
            throw new RuntimeException("Bu kitab hazırda mövcud deyil: " + book.getTitle());
        }

        // İstifadəçi eyni kitabı artıq götürüb?
        borrowRecordRepository.findByUserIdAndBookIdAndStatus(userId, book.getId(), BorrowStatus.BORROWED)
                .ifPresent(br -> {
                    throw new RuntimeException("Siz bu kitabı artıq götürmüsünüz");
                });

        // Borrow qeydi yarat
        BorrowRecord record = BorrowRecord.builder()
                .user(user)
                .book(book)
                .dueDate(request.getDueDate())
                .notes(request.getNotes())
                .build();

        // Stoku azalt
        book.setAvailableStock(book.getAvailableStock() - 1);
        bookRepository.save(book);

        return toResponse(borrowRecordRepository.save(record));
    }

    // Kitab qaytar
    @Transactional
    public BorrowResponse returnBook(Long borrowId, String notes) {
        BorrowRecord record = borrowRecordRepository.findById(borrowId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow qeydi tapılmadı: " + borrowId));

        if (record.getStatus() == BorrowStatus.RETURNED) {
            throw new RuntimeException("Bu kitab artıq qaytarılıb");
        }

        // Stoku artır
        Book book = record.getBook();
        book.setAvailableStock(book.getAvailableStock() + 1);
        bookRepository.save(book);

        // Qeydi yenilə
        record.setStatus(BorrowStatus.RETURNED);
        record.setReturnDate(LocalDateTime.now());
        if (notes != null) record.setNotes(notes);

        return toResponse(borrowRecordRepository.save(record));
    }

    // Öz tarixçəm
    public Page<BorrowResponse> getMyHistory(Long userId, Pageable pageable) {
        return borrowRecordRepository.findByUserId(userId, pageable).map(this::toResponse);
    }

    // Bütün borrow-lar (ADMIN/LIBRARIAN)
    public Page<BorrowResponse> getAllBorrows(Pageable pageable) {
        return borrowRecordRepository.findAll(pageable).map(this::toResponse);
    }

    // Statusuna görə
    public Page<BorrowResponse> getBorrowsByStatus(BorrowStatus status, Pageable pageable) {
        return borrowRecordRepository.findByStatus(status, pageable).map(this::toResponse);
    }

    // Hər gecə müddəti keçmiş kitabları yenilə (Cron Job)
    @Scheduled(cron = "0 0 0 * * *") // Hər gecə 00:00
    @Transactional
    public void updateOverdueRecords() {
        List<BorrowRecord> overdueRecords = borrowRecordRepository.findOverdueRecords(LocalDate.now());
        overdueRecords.forEach(record -> record.setStatus(BorrowStatus.OVERDUE));
        borrowRecordRepository.saveAll(overdueRecords);
    }

    // Entity → DTO
    private BorrowResponse toResponse(BorrowRecord record) {
        boolean isOverdue = record.getStatus() == BorrowStatus.BORROWED
                && record.getDueDate().isBefore(LocalDate.now());

        return BorrowResponse.builder()
                .id(record.getId())
                .userId(record.getUser().getId())
                .userFullName(record.getUser().getFullName())
                .bookId(record.getBook().getId())
                .bookTitle(record.getBook().getTitle())
                .bookIsbn(record.getBook().getIsbn())
                .borrowDate(record.getBorrowDate())
                .dueDate(record.getDueDate())
                .returnDate(record.getReturnDate())
                .status(record.getStatus())
                .notes(record.getNotes())
                .isOverdue(isOverdue)
                .build();
    }
}

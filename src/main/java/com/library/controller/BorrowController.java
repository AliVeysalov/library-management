package com.library.controller;

import com.library.dto.borrow.BorrowRequest;
import com.library.dto.borrow.BorrowResponse;
import com.library.entity.User;
import com.library.entity.enums.BorrowStatus;
import com.library.service.BorrowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrow")
@RequiredArgsConstructor
@Tag(name = "Borrow", description = "Kitab götürmə/qaytarma API-ları")
@SecurityRequirement(name = "bearerAuth")
public class BorrowController {

    private final BorrowService borrowService;

    @PostMapping
    @PreAuthorize("hasRole('MEMBER')")
    @Operation(summary = "Kitab götür")
    public ResponseEntity<BorrowResponse> borrowBook(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody BorrowRequest request) {
        return ResponseEntity.ok(borrowService.borrowBook(currentUser.getId(), request));
    }

    @PutMapping("/return/{borrowId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Kitab qaytar")
    public ResponseEntity<BorrowResponse> returnBook(
            @PathVariable Long borrowId,
            @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(borrowService.returnBook(borrowId, notes));
    }

    @GetMapping("/my-history")
    @PreAuthorize("hasRole('MEMBER')")
    @Operation(summary = "Öz kitab tarixçəm")
    public ResponseEntity<Page<BorrowResponse>> getMyHistory(
            @AuthenticationPrincipal User currentUser,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(borrowService.getMyHistory(currentUser.getId(), pageable));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Bütün borrow qeydləri (ADMIN/LIBRARIAN)")
    public ResponseEntity<Page<BorrowResponse>> getAllBorrows(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(borrowService.getAllBorrows(pageable));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Statusa görə borrow qeydləri")
    public ResponseEntity<Page<BorrowResponse>> getBorrowsByStatus(
            @PathVariable BorrowStatus status,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(borrowService.getBorrowsByStatus(status, pageable));
    }
}

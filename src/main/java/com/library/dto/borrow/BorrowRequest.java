package com.library.dto.borrow;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BorrowRequest {

    @NotNull(message = "Kitab ID boş ola bilməz")
    private Long bookId;

    @NotNull(message = "Son qaytarma tarixi boş ola bilməz")
    @Future(message = "Son tarix gələcəkdə olmalıdır")
    private LocalDate dueDate;

    private String notes;
}

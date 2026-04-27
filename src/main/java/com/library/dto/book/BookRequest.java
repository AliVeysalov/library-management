package com.library.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BookRequest {

    @NotBlank(message = "Kitab adı boş ola bilməz")
    private String title;

    @NotBlank(message = "Müəllif adı boş ola bilməz")
    private String author;

    @NotBlank(message = "ISBN boş ola bilməz")
    private String isbn;

    private String publisher;

    private Integer publishYear;

    private String description;

    @NotNull(message = "Stok sayı boş ola bilməz")
    @Positive(message = "Stok sayı müsbət olmalıdır")
    private Integer totalStock;

    private Long categoryId;
}

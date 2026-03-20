package com.library.management.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BookRequest(
        @NotBlank(message = "ISBN must not be blank")
        @Size(max = 20, message = "ISBN must be at most 20 characters")
        String isbn,
        @NotBlank(message = "Title must not be blank")
        @Size(max = 255, message = "Title must be at most 255 characters")
        String title,
        @NotBlank(message = "Author must not be blank")
        @Size(max = 255, message = "Author must be at most 255 characters")
        String author
) {}

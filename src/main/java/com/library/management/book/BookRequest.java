package com.library.management.book;

import jakarta.validation.constraints.NotBlank;

public record BookRequest(
        @NotBlank String isbn,
        @NotBlank String title,
        @NotBlank String author
) {}

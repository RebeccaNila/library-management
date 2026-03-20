package com.library.management.book;

public record BookResponse(
        Long id,
        String isbn,
        String title,
        String author
) {}

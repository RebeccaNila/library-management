package com.library.management.book;

import java.util.UUID;

public record BookResponse(
        UUID id,
        String isbn,
        String title,
        String author
) {}

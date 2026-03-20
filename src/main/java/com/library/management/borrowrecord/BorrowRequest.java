package com.library.management.borrowrecord;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BorrowRequest(
        @NotNull(message = "Book ID is required") UUID bookId,
        @NotNull(message = "Borrower ID is required") UUID borrowerId,
        String message
) {}
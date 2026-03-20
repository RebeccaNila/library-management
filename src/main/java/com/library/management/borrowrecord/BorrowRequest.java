package com.library.management.borrowrecord;

import jakarta.validation.constraints.NotNull;

public record BorrowRequest(
        @NotNull(message = "Book ID is required") Long bookId,
        @NotNull(message = "Borrower ID is required") Long borrowerId,
        String message
) {}
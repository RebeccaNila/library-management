package com.library.management.borrowrecord;

import java.util.UUID;

import java.time.LocalDateTime;


public record BorrowResponse(
        UUID loanId,
        Long bookId,
        Long borrowerId,
        String status,
        String message,
        LocalDateTime timestamp

) { }

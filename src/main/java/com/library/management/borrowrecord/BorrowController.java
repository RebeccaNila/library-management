package com.library.management.borrowrecord;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/loans")
@Tag(name = "Loan Management", description = "Actions for borrowing and returning books")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;

    @PostMapping()
    @Operation(summary = "Borrow a book", description = "Links a physical Book ID to a Borrower ID")
    public ResponseEntity<BorrowResponse> borrowBook(@Valid @RequestBody BorrowRequest request) {
        return new ResponseEntity<>(borrowService.borrowBook(request), HttpStatus.CREATED);
    }

    @PostMapping("/{loanId}/return")
    @Operation(summary = "Return a book", description = "Marks a specific Loan ID as returned")
    public ResponseEntity<BorrowResponse> returnBook(@PathVariable UUID loanId) {
        return ResponseEntity.ok(borrowService.returnBook(loanId));
    }
}

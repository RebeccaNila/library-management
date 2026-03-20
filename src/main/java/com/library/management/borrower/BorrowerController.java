package com.library.management.borrower;

import com.library.management.book.BookResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/borrowers")
@Tag(name = "Borrowers Management", description = "Actions for creating and listing borrowers")
@RequiredArgsConstructor
public class BorrowerController {

    private final BorrowerService borrowerService;

    @PostMapping
    public ResponseEntity<BorrowerResponse> register(
            @Valid @RequestBody BorrowerRequest request) {

        BorrowerResponse response = borrowerService.registerBorrower(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<BorrowerResponse>> getAllBooks() {
        return ResponseEntity.ok(borrowerService.getAllBorrowers());
    }
}

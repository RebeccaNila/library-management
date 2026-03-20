package com.library.management.borrower;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrowers")
@RequiredArgsConstructor
public class BorrowerController {

    private final BorrowerService borrowerService;

    @PostMapping
    public ResponseEntity<BorrowerResponse> register(
            @Valid @RequestBody BorrowerRequest request) {

        BorrowerResponse response = borrowerService.registerBorrower(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

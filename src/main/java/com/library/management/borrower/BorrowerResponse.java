package com.library.management.borrower;
import java.util.UUID;

public record BorrowerResponse(
        UUID id,
        String name,
        String email

) { }

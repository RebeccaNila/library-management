package com.library.management.borrowrecord;

import java.util.UUID;

public interface BorrowService {

    BorrowResponse borrowBook(BorrowRequest request);
    BorrowResponse returnBook(UUID bookId);
}

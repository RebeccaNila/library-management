package com.library.management.borrowrecord;

import com.library.management.book.Book;
import com.library.management.book.BookRepository;
import com.library.management.borrower.Borrower;
import com.library.management.borrower.BorrowerRepository;
import com.library.management.common.exception.ConflictException;
import com.library.management.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.UUID;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRepository borrowRepository;
    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;

    @Override
    public BorrowResponse borrowBook(BorrowRequest request) {
        log.info("Attempting to borrow book ID: {} by borrower ID: {}", request.bookId(), request.borrowerId());

        // 1. Validate Book existence
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + request.bookId()));

        // 2. Validate Borrower existence
        Borrower borrower = borrowerRepository.findById(request.borrowerId())
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found with ID: " + request.borrowerId()));

        // 3. Requirement #8: Is the book already borrowed?
        if (borrowRepository.existsByBookIdAndReturnedAtIsNull(request.bookId())) {
            log.warn("Book ID {} is already borrowed", request.bookId());
            throw new ConflictException("This book copy is already borrowed by another member.");
        }

        // 4. Save Record
        BorrowRecord record = new BorrowRecord();
        record.setBook(book);
        record.setBorrower(borrower);
        record.setBorrowedAt(LocalDateTime.now());


        log.info("Book ID {} successfully borrowed by {}", request.bookId(), borrower.getName());
        BorrowRecord saved = borrowRepository.save(record);

        return new BorrowResponse(
                saved.getLoadId(),
                saved.getBook().getId(),
                saved.getBorrower().getId(),
                saved.getStatus().name(),
                "Book borrowed successfully.",
                saved.getBorrowedAt()
        );
    }

    @Override
    public BorrowResponse returnBook(UUID loanId) {
        log.info("Processing return for Borrow Record ID: {}", loanId);

        // 1. Check if Borrow ID is valid (Exists in DB)
        BorrowRecord record = borrowRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("No borrow record found with ID: " + loanId));

        // 2. Check if already returned (Avoid "Double Return" logic errors)
        if (LoanStatus.RETURNED.equals(record.getStatus())) {
            log.warn("Attempted to return an already returned book. BorrowID: {}", loanId);
            throw new ConflictException("This book is already returned.");
        }

        // 3. Update the record
        record.setStatus(LoanStatus.RETURNED);
        record.setReturnedAt(LocalDateTime.now());

        // 4. (Optional but Recommended) Update Book availability if you added 'isAvailable' to Book Entity
//        record.getBook().setAvailable(true);

        BorrowRecord saved = borrowRepository.save(record);
        log.info("Borrow Record ID: {} successfully updated to RETURNED", loanId);

        return new BorrowResponse(
                saved.getLoadId(),
                saved.getBook().getId(),
                saved.getBorrower().getId(),
                saved.getStatus().name(),
                "Borrowed book returned successfully.",
                saved.getReturnedAt()
        );
    }
}

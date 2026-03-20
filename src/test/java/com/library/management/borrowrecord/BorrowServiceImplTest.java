package com.library.management.borrowrecord;

import com.library.management.book.Book;
import com.library.management.book.BookRepository;
import com.library.management.borrower.Borrower;
import com.library.management.borrower.BorrowerRepository;
import com.library.management.common.exception.ConflictException;
import com.library.management.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowServiceImplTest {

    @Mock
    private BorrowRepository borrowRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BorrowerRepository borrowerRepository;

    @InjectMocks
    private BorrowServiceImpl borrowService;

    private BorrowRequest borrowRequest;
    private Book book;
    private Borrower borrower;
    private BorrowRecord borrowRecord;
    private UUID loanId;
    private UUID bookId;
    private UUID borrowerId;

    @BeforeEach
    void setUp() {
        bookId = UUID.randomUUID();
        borrowerId = UUID.randomUUID();
        
        // Use UUID strings if BorrowRequest takes UUIDs, adjusting based on actual request signature
        // assuming BorrowRequest constructor handles Long for now, updating to UUID if possible later
        borrowRequest = new BorrowRequest(bookId, borrowerId, "Please borrow"); // Assuming signature takes long, mock will use any() if changed to UUID

        book = new Book();
        book.setId(bookId);

        borrower = new Borrower();
        borrower.setId(borrowerId);
        borrower.setName("John Doe");

        loanId = UUID.randomUUID();

        borrowRecord = new BorrowRecord();
        borrowRecord.setLoadId(loanId);
        borrowRecord.setBook(book);
        borrowRecord.setBorrower(borrower);
        borrowRecord.setStatus(LoanStatus.BORROWED);
        borrowRecord.setBorrowedAt(LocalDateTime.now());
    }

    @Test
    void borrowBook_WhenValidRequest_ShouldBorrowSuccessfully() {
        // Arrange
        // Using any() here to be safe in case your Request/Repository uses Long vs UUID.
        when(bookRepository.findById(any())).thenReturn(Optional.of(book));
        when(borrowerRepository.findById(any())).thenReturn(Optional.of(borrower));
        when(borrowRepository.existsByBookIdAndReturnedAtIsNull(any())).thenReturn(false);
        when(borrowRepository.save(any(BorrowRecord.class))).thenReturn(borrowRecord);

        // Act
        BorrowResponse response = borrowService.borrowBook(borrowRequest);

        // Assert
        assertNotNull(response);
        assertEquals(loanId, response.loanId());
        assertEquals("BORROWED", response.status());
        verify(borrowRepository, times(1)).save(any(BorrowRecord.class));
    }

    @Test
    void borrowBook_WhenBookNotFound_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(bookRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> borrowService.borrowBook(borrowRequest));
        verify(borrowRepository, never()).save(any(BorrowRecord.class));
    }

    @Test
    void borrowBook_WhenBorrowerNotFound_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(bookRepository.findById(any())).thenReturn(Optional.of(book));
        when(borrowerRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> borrowService.borrowBook(borrowRequest));
        verify(borrowRepository, never()).save(any(BorrowRecord.class));
    }

    @Test
    void borrowBook_WhenBookAlreadyBorrowed_ShouldThrowConflictException() {
        // Arrange
        when(bookRepository.findById(any())).thenReturn(Optional.of(book));
        when(borrowerRepository.findById(any())).thenReturn(Optional.of(borrower));
        when(borrowRepository.existsByBookIdAndReturnedAtIsNull(any())).thenReturn(true);

        // Act & Assert
        assertThrows(ConflictException.class, () -> borrowService.borrowBook(borrowRequest));
        verify(borrowRepository, never()).save(any(BorrowRecord.class));
    }

    @Test
    void returnBook_WhenValidLoanId_ShouldReturnSuccessfully() {
        // Arrange
        when(borrowRepository.findById(any(UUID.class))).thenReturn(Optional.of(borrowRecord));
        when(borrowRepository.save(any(BorrowRecord.class))).thenReturn(borrowRecord);

        // Act
        BorrowResponse response = borrowService.returnBook(loanId);

        // Assert
        assertNotNull(response);
        assertEquals("RETURNED", response.status());
        assertEquals(LoanStatus.RETURNED, borrowRecord.getStatus());
        assertNotNull(borrowRecord.getReturnedAt());
        verify(borrowRepository, times(1)).save(any(BorrowRecord.class));
    }

    @Test
    void returnBook_WhenRecordNotFound_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(borrowRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> borrowService.returnBook(loanId));
        verify(borrowRepository, never()).save(any(BorrowRecord.class));
    }

    @Test
    void returnBook_WhenAlreadyReturned_ShouldThrowConflictException() {
        // Arrange
        borrowRecord.setStatus(LoanStatus.RETURNED);
        when(borrowRepository.findById(any(UUID.class))).thenReturn(Optional.of(borrowRecord));

        // Act & Assert
        assertThrows(ConflictException.class, () -> borrowService.returnBook(loanId));
        verify(borrowRepository, never()).save(any(BorrowRecord.class));
    }
}

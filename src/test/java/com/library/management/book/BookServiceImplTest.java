package com.library.management.book;

import com.library.management.common.exception.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private BookRequest validRequest;
    private Book existingBook;
    private UUID bookId;

    @BeforeEach
    void setUp() {
        bookId = UUID.randomUUID();
        validRequest = new BookRequest("1234567890", "Test Title", "Test Author");

        existingBook = new Book();
        existingBook.setId(bookId);
        existingBook.setIsbn("1234567890");
        existingBook.setTitle("Test Title");
        existingBook.setAuthor("Test Author");
    }

    @Test
    void saveBook_WhenNewIsbn_ShouldSaveSuccessfully() {
        // Arrange
        when(bookRepository.findFirstByIsbn(anyString())).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(existingBook);

        // Act
        BookResponse response = bookService.saveBook(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals("1234567890", response.isbn());
        assertEquals("Test Title", response.title());
        assertEquals(bookId, response.id());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void saveBook_WhenExistingIsbnButMatchingTitleAndAuthor_ShouldSaveSuccessfully() {
        // Arrange
        when(bookRepository.findFirstByIsbn(anyString())).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(existingBook);

        // Act
        BookResponse response = bookService.saveBook(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(bookId, response.id());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void saveBook_WhenExistingIsbnAndDifferentTitle_ShouldThrowConflictException() {
        // Arrange
        existingBook.setTitle("Different Title");
        when(bookRepository.findFirstByIsbn(anyString())).thenReturn(Optional.of(existingBook));

        // Act & Assert
        assertThrows(ConflictException.class, () -> bookService.saveBook(validRequest));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void saveBook_WhenExistingIsbnAndDifferentAuthor_ShouldThrowConflictException() {
        // Arrange
        existingBook.setAuthor("Different Author");
        when(bookRepository.findFirstByIsbn(anyString())).thenReturn(Optional.of(existingBook));

        // Act & Assert
        assertThrows(ConflictException.class, () -> bookService.saveBook(validRequest));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void getAllBooks_ShouldReturnListOfResponses() {
        // Arrange
        when(bookRepository.findAll()).thenReturn(List.of(existingBook));

        // Act
        List<BookResponse> responses = bookService.getAllBooks();

        // Assert
        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        assertEquals("1234567890", responses.get(0).isbn());
        assertEquals(bookId, responses.get(0).id());
    }
}

package com.library.management.borrower;

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
class BorrowerServiceImplTest {

    @Mock
    private BorrowerRepository borrowerRepository;

    @InjectMocks
    private BorrowerServiceImpl borrowerService;

    private BorrowerRequest validRequest;
    private Borrower existingBorrower;
    private UUID borrowerId;

    @BeforeEach
    void setUp() {
        borrowerId = UUID.randomUUID();
        validRequest = new BorrowerRequest("John Doe", "john@test.com");

        existingBorrower = new Borrower();
        existingBorrower.setId(borrowerId);
        existingBorrower.setName("John Doe");
        existingBorrower.setEmail("john@test.com");
    }

    @Test
    void registerBorrower_WhenNewEmail_ShouldRegisterSuccessfully() {
        // Arrange
        when(borrowerRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(borrowerRepository.save(any(Borrower.class))).thenReturn(existingBorrower);

        // Act
        BorrowerResponse response = borrowerService.registerBorrower(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals("john@test.com", response.email());
        assertEquals("John Doe", response.name());
        assertEquals(borrowerId, response.id());
        verify(borrowerRepository, times(1)).save(any(Borrower.class));
    }

    @Test
    void registerBorrower_WhenEmailAlreadyExists_ShouldThrowConflictException() {
        // Arrange
        when(borrowerRepository.findByEmail(anyString())).thenReturn(Optional.of(existingBorrower));

        // Act & Assert
        assertThrows(ConflictException.class, () -> borrowerService.registerBorrower(validRequest));
        verify(borrowerRepository, never()).save(any(Borrower.class));
    }

    @Test
    void getAllBorrowers_ShouldReturnListOfResponses() {
        // Arrange
        when(borrowerRepository.findAll()).thenReturn(List.of(existingBorrower));

        // Act
        List<BorrowerResponse> responses = borrowerService.getAllBorrowers();

        // Assert
        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        assertEquals("john@test.com", responses.get(0).email());
        assertEquals(borrowerId, responses.get(0).id());
    }
}

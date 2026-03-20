package com.library.management.borrowrecord;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BorrowController.class)
class BorrowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BorrowService borrowService;

    @Test
    void borrowBook_ShouldReturnCreated() throws Exception {
        // Arrange
        UUID loanId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        UUID borrowerId = UUID.randomUUID();
        
        BorrowRequest request = new BorrowRequest(bookId, borrowerId, "Test Message");
        
        BorrowResponse response = new BorrowResponse(
                loanId, bookId, borrowerId, "BORROWED", "Success", LocalDateTime.now()
        );

        when(borrowService.borrowBook(any(BorrowRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.loanId").value(loanId.toString()))
                .andExpect(jsonPath("$.status").value("BORROWED"))
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void returnBook_ShouldReturnOk() throws Exception {
        // Arrange
        UUID loanId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        UUID borrowerId = UUID.randomUUID();
        
        BorrowResponse response = new BorrowResponse(
                loanId, bookId, borrowerId, "RETURNED", "Success", LocalDateTime.now()
        );

        when(borrowService.returnBook(any(UUID.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/loans/{loanId}/return", loanId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loanId").value(loanId.toString()))
                .andExpect(jsonPath("$.status").value("RETURNED"))
                .andExpect(jsonPath("$.message").value("Success"));
    }
}

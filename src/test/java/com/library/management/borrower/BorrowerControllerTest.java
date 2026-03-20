package com.library.management.borrower;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BorrowerController.class)
class BorrowerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BorrowerService borrowerService;

    @Test
    void registerBorrower_ShouldReturnCreated() throws Exception {
        // Arrange
        UUID borrowerId = UUID.randomUUID();
        BorrowerRequest request = new BorrowerRequest("John Doe", "john@test.com");
        BorrowerResponse response = new BorrowerResponse(borrowerId, "John Doe", "john@test.com");

        when(borrowerService.registerBorrower(any(BorrowerRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/borrowers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(borrowerId.toString()))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@test.com"));
    }

    @Test
    void getAllBorrowers_ShouldReturnOk() throws Exception {
        // Arrange
        UUID borrowerId = UUID.randomUUID();
        BorrowerResponse response = new BorrowerResponse(borrowerId, "John Doe", "john@test.com");
        when(borrowerService.getAllBorrowers()).thenReturn(List.of(response));

        // Act & Assert
        mockMvc.perform(get("/api/v1/borrowers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john@test.com"));
    }
}

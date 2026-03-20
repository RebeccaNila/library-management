package com.library.management.book;

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

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    @Test
    void createBook_ShouldReturnCreated() throws Exception {
        // Arrange
        UUID bookId = UUID.randomUUID();
        BookRequest request = new BookRequest("1234567890", "Test Title", "Test Author");
        BookResponse response = new BookResponse(bookId, "1234567890", "Test Title", "Test Author");

        when(bookService.saveBook(any(BookRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(bookId.toString()))
                .andExpect(jsonPath("$.isbn").value("1234567890"))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.author").value("Test Author"));
    }

    @Test
    void createBook_WhenIsbnIsBlank_ShouldReturnBadRequest() throws Exception {
        // Arrange
        BookRequest request = new BookRequest("", "Test Title", "Test Author");

        // Act & Assert
        mockMvc.perform(post("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.isbn").value("ISBN must not be blank"));
    }

    @Test
    void createBook_WhenTitleIsBlank_ShouldReturnBadRequest() throws Exception {
        // Arrange
        BookRequest request = new BookRequest("1234567890", "", "Test Author");

        // Act & Assert
        mockMvc.perform(post("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").value("Title must not be blank"));
    }

    @Test
    void createBook_WhenAuthorIsBlank_ShouldReturnBadRequest() throws Exception {
        // Arrange
        BookRequest request = new BookRequest("1234567890", "Test Title", "");

        // Act & Assert
        mockMvc.perform(post("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.author").value("Author must not be blank"));
    }

    @Test
    void createBook_WhenIsbnIsTooLong_ShouldReturnBadRequest() throws Exception {
        // Arrange
        String longIsbn = "1".repeat(21); // Exceeds max length of 20
        BookRequest request = new BookRequest(longIsbn, "Test Title", "Test Author");

        // Act & Assert
        mockMvc.perform(post("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.isbn").value("ISBN must be at most 20 characters"));
    }

    @Test
    void getAllBooks_ShouldReturnOk() throws Exception {
        // Arrange
        UUID bookId = UUID.randomUUID();
        BookResponse response = new BookResponse(bookId, "1234567890", "Test Title", "Test Author");
        when(bookService.getAllBooks()).thenReturn(List.of(response));

        // Act & Assert
        mockMvc.perform(get("/api/v1/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].isbn").value("1234567890"));
    }
}

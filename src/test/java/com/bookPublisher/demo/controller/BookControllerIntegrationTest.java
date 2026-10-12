package com.bookPublisher.demo.controller;

import com.bookPublisher.demo.dto.AuthRequestDTO;
import com.bookPublisher.demo.dto.BookRequest;
import com.bookPublisher.demo.dto.JwtResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthController authController;

    private String accessToken;

    @BeforeEach
    void setUp() {
        AuthRequestDTO authRequest = new AuthRequestDTO();
        authRequest.setUsername("admin");
        authRequest.setPassword("admin");

        if (Optional.ofNullable(accessToken).isEmpty()) {
            JwtResponseDTO jwtResponse = authController.authenticateAndGetToken(authRequest);
            accessToken = jwtResponse.getAccessToken();
        }
    }

    @Test
    void testGetAll() throws Exception {
        mockMvc.perform(get("/books")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.content[0].title", is("The Great Gatsby")))
                .andExpect(jsonPath("$.content[1].title", is("1984")))
                .andExpect(jsonPath("$.content[2].title", is("Pride and Prejudice")));
    }

    @Test
    void testGetBookByIsbn() throws Exception {
        String isbn = "9780743273565";

        mockMvc.perform(get("/books/{isbn}", isbn)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("The Great Gatsby")))
                .andExpect(jsonPath("$.isbn", is(isbn)))
                .andExpect(jsonPath("$.pages", is(180)))
                .andExpect(jsonPath("$.publisherId", is(1)));
    }

    @Test
    void testSaveBook() throws Exception {
        BookRequest request = new BookRequest(
                "Clean Code",
                "9780132350884",
                464,
                new BigDecimal("35.99"),
                1L
        );

        mockMvc.perform(post("/books")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Clean Code")))
                .andExpect(jsonPath("$.isbn", is("9780132350884")))
                .andExpect(jsonPath("$.pages", is(464)))
                .andExpect(jsonPath("$.price", is(35.99)))
                .andExpect(jsonPath("$.publisherId", is(1)));
    }

    @Test
    void testUpdateBook() throws Exception {
        String isbn = "9780743273565";

        BookRequest request = new BookRequest(
                "The Great Gatsby - Updated",
                isbn,
                200,
                new BigDecimal("17.99"),
                1L
        );

        mockMvc.perform(put("/books/{isbn}", isbn)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("The Great Gatsby - Updated")))
                .andExpect(jsonPath("$.isbn", is(isbn)))
                .andExpect(jsonPath("$.pages", is(200)))
                .andExpect(jsonPath("$.price", is(17.99)))
                .andExpect(jsonPath("$.publisherId", is(1)));
    }

    @Test
    void testDeleteBook() throws Exception {
        String isbn = "9780743273565";

        mockMvc.perform(delete("/books/{isbn}", isbn)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());
    }
}

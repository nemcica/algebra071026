package com.bookPublisher.demo.controller;

import com.bookPublisher.demo.dto.BookRequest;
import com.bookPublisher.demo.dto.BookResponse;
import com.bookPublisher.demo.service.BookService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private MockMvc mockMvc;

    Page<BookResponse> books;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();

        books = new PageImpl<>(
                List.of(
                        new BookResponse(
                                1L, "Book 1", "9780525559474",
                                304, new BigDecimal("18.99"), 1L
                        ),
                        new BookResponse(
                                2L, "Book 2", "9780735211292",
                                320, new BigDecimal("21.99"), 2L
                        )
                ),
                PageRequest.of(0, 10),
                2
        );
    }

    @Test
    void testGetAllBooks() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);

        when(bookService.findAll(pageable)).thenReturn(books);

        mockMvc.perform(get("/books")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title", is("Book 1")))
                .andExpect(jsonPath("$.content[1].title", is("Book 2")));

        verify(bookService, times(1)).findAll(pageable);
    }

    @Test
    void testGetBookByIsbnFound() throws Exception {
        BookResponse book = books.getContent().getFirst();
        String isbn = book.isbn();

        when(bookService.findByIsbn(isbn)).thenReturn(Optional.of(book));

        mockMvc.perform(get("/books/{isbn}", isbn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.isbn", is(isbn)))
                .andExpect(jsonPath("$.title", is("Book 1")));

        verify(bookService, times(1)).findByIsbn(isbn);
    }

    @Test
    void testGetBookByIsbnNotFound() throws Exception {
        String isbn = "0000000000000";

        when(bookService.findByIsbn(isbn)).thenReturn(Optional.empty());

        mockMvc.perform(get("/books/{isbn}", isbn))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).findByIsbn(isbn);
    }

    @Test
    void testSaveSuccess() throws Exception {
        BookRequest request = new BookRequest("Test book", "000000000", 100, new BigDecimal(100), 1L);
        BookResponse response = new BookResponse(1L,"Test book", "000000000", 100, new BigDecimal(100), 1L);

        when(bookService.createBook(request)).thenReturn(response);

        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test book")))
                .andExpect(jsonPath("$.isbn", is("000000000")));

        verify(bookService, times(1)).createBook(request);

    }

    @Test
    void testSaveConflict() throws Exception {
        BookRequest request = new BookRequest(
                "The Midnight Library",
                "9780525559111",
                304,
                new BigDecimal("18.99"),
                1L);

        when(bookService.createBook(request))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Isbn: 9780525559111 already exists."));

        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(bookService, times(1)).createBook(request);
    }

    @Test
    void testUpdateSuccess() throws Exception {
        BookRequest request = new BookRequest(
                "The Midnight Library PUT",
                "9780525559111",
                304,
                new BigDecimal("18.99"),
                1L);

        BookResponse response = new BookResponse(
                1L,
                "The Midnight Library PUT",
                "9780525559111",
                304,
                new BigDecimal("18.99"),
                1L);

        String isbn = "9780525559111";

        when(bookService.updateBook(request, isbn)).thenReturn(response);

        mockMvc.perform(put("/books/{isbn}", isbn)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("The Midnight Library PUT")))
                .andExpect(jsonPath("$.isbn", is ("9780525559111")));

        verify(bookService, times(1)).updateBook(request, isbn);
    }

    @Test
    void testUpdateNotFound() throws Exception {
        BookRequest request = new BookRequest(
                "Unknown book",
                "000000000",
                10,
                new BigDecimal("10"),
                1L);
        String isbn = "000000000";

        when(bookService.updateBook(request, isbn)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Isbn: 000000000 does not exist."));

        mockMvc.perform(put("/books/{isbn}", isbn)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).updateBook(request, isbn);
    }

    @Test
    void testDelete() throws Exception {
        String isbn = "1234567890";

        mockMvc.perform(delete("/books/{isbn}", isbn))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteByIsbn(isbn);
    }



}

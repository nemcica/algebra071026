package com.bookPublisher.demo.service;

import com.bookPublisher.demo.dto.BookRequest;
import com.bookPublisher.demo.dto.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookService {

    Page<BookResponse> findAll(Pageable pageable);

    Optional<BookResponse> findByIsbn(String isbn);

    BookResponse createBook(BookRequest request);

    BookResponse updateBook(BookRequest request, String isbn);

    void deleteByIsbn(String isbn);

}

package com.bookPublisher.demo.service.impl;

import com.bookPublisher.demo.domain.Book;
import com.bookPublisher.demo.domain.Publisher;
import com.bookPublisher.demo.dto.BookRequest;
import com.bookPublisher.demo.dto.BookResponse;
import com.bookPublisher.demo.mapper.BookMapper;
import com.bookPublisher.demo.repository.BookRepository;
import com.bookPublisher.demo.repository.PublisherRepository;
import com.bookPublisher.demo.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;

    @Override
    public Page<BookResponse> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).map(bookMapper::toResponse);
    }

    @Override
    public Optional<BookResponse> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn).map(bookMapper::toResponse);
    }

    @Override
    @Transactional
    public BookResponse createBook(BookRequest request) {
        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Isbn: " + request.isbn() + " aready exists.");
        }

        Publisher publisher = publisherRepository.findById(request.publisherId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Publisher with id: " + request.publisherId() + " does not exist."));

        Book book = bookMapper.toEntity(request, publisher);

        return bookMapper.toResponse(bookRepository.save(book));
    }

    @Override
    @Transactional
    public BookResponse updateBook(BookRequest request, String isbn) {
        Book book = bookRepository.findByIsbn(isbn).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Isbn: " + isbn + " not found."));

        Publisher publisher = publisherRepository.findById(request.publisherId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Publisher with id: " + request.publisherId() + " does not exist."));

        bookMapper.updateBookFromRequest(book, request, publisher);

        return bookMapper.toResponse(bookRepository.save(book));
    }

    @Override
    @Transactional
    public void deleteByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Isbn: " + isbn + " not found."));

        bookRepository.delete(book);
    }
}

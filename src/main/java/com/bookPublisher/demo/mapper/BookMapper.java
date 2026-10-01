package com.bookPublisher.demo.mapper;

import com.bookPublisher.demo.domain.Book;
import com.bookPublisher.demo.domain.Publisher;
import com.bookPublisher.demo.dto.BookRequest;
import com.bookPublisher.demo.dto.BookResponse;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public Book toEntity(BookRequest request, Publisher publisher) {
        Book book = new Book();

        book.setTitle(request.title());
        book.setIsbn(request.isbn());
        book.setPages(request.pages());
        book.setPrice(request.price());
        book.setPublisher(publisher);

        return book;
    }

    public BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPages(),
                book.getPrice(),
                book.getPublisher().getId()
        );
    }

    public void updateBookFromRequest(Book book, BookRequest request, Publisher publisher) {
        book.setTitle(request.title());
        book.setIsbn(request.isbn());
        book.setPages(request.pages());
        book.setPrice(request.price());
        book.setPublisher(publisher);

    }
}

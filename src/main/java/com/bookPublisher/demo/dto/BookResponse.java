package com.bookPublisher.demo.dto;

import java.math.BigDecimal;

public record BookResponse(
        Long id,

        String title,

        String isbn,

        Integer pages,

        BigDecimal price,

        Long publisherId
) {
}

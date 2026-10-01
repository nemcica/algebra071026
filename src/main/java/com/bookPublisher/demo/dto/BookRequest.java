package com.bookPublisher.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record BookRequest(
        @NotBlank
        String title,

        @NotBlank
        String isbn,

        @Positive
        Integer pages,

        @Positive
        BigDecimal price,

        Long publisherId
) {
}

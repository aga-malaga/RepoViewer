package com.example.task.common.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

    private final int statusCode;

    public NotFoundException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
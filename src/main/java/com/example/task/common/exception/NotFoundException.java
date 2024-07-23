package com.example.task.common.exception;

import lombok.Getter;

public class NotFoundException extends RuntimeException {

    @Getter
    private final int status;

    public NotFoundException(String message, int status) {
        super(message);
        this.status = status;
    }
}
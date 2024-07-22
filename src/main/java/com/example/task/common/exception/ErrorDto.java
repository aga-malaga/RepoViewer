package com.example.task.common.exception;

public record ErrorDto(
        int status,
        String message
) {
}
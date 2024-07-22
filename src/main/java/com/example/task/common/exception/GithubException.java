package com.example.task.common.exception;

import lombok.Getter;

public class GithubException extends RuntimeException {

    private static final String MESSAGE_FORMAT = "Github exception: %s";

    @Getter
    private final int status;

    public GithubException(String message, int status) {
        super(MESSAGE_FORMAT.formatted(message));
        this.status = status;
    }
}
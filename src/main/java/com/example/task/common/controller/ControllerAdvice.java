package com.example.task.common.controller;

import com.example.task.common.exception.ErrorDto;
import com.example.task.common.exception.GithubException;
import com.example.task.common.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
class ControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    ErrorDto handleNotFoundException(NotFoundException exception) {
        log.error(exception.getMessage());
        return new ErrorDto(exception.getStatus(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(GithubException.class)
    ErrorDto handleGithubException(GithubException exception) {
        log.error(exception.getMessage());
        return new ErrorDto(exception.getStatus(), exception.getMessage());
    }
}
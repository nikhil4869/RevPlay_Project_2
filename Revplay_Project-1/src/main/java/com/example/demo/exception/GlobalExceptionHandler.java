package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(
                new ApiError(ex.getMessage(), 404, LocalDateTime.now()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateResourceException ex) {
        return new ResponseEntity<>(
                new ApiError(ex.getMessage(), 409, LocalDateTime.now()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorized(UnauthorizedException ex) {
        return new ResponseEntity<>(
                new ApiError(ex.getMessage(), 401, LocalDateTime.now()),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {
        return new ResponseEntity<>(
                new ApiError(ex.getMessage(), 400, LocalDateTime.now()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidation(ValidationException ex) {
        return new ResponseEntity<>(
                new ApiError(ex.getMessage(), 422, LocalDateTime.now()),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ApiError> handleFile(FileStorageException ex) {
        return new ResponseEntity<>(
                new ApiError(ex.getMessage(), 500, LocalDateTime.now()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGlobal(Exception ex) {
        return new ResponseEntity<>(
                new ApiError("Something went wrong", 500, LocalDateTime.now()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    
}

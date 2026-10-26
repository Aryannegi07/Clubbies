package com.clubbies.common;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ApiError> notFound(NotFoundException ex) { return ResponseEntity.status(404).body(ApiError.of(404, "NOT_FOUND", ex.getMessage())); }
    @ExceptionHandler({ForbiddenException.class, AccessDeniedException.class})
    ResponseEntity<ApiError> forbidden(Exception ex) { return ResponseEntity.status(403).body(ApiError.of(403, "FORBIDDEN", "You do not have permission for this resource.")); }
    @ExceptionHandler(ConflictException.class)
    ResponseEntity<ApiError> conflict(ConflictException ex) { return ResponseEntity.status(409).body(ApiError.of(409, "CONFLICT", ex.getMessage())); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex) {
        var fields = ex.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(
                e -> e.getField(), e -> e.getDefaultMessage() == null ? "Invalid value" : e.getDefaultMessage(), (a, b) -> a));
        return ResponseEntity.badRequest().body(new ApiError(java.time.Instant.now(), 400, "VALIDATION_ERROR", "Please correct the highlighted fields.", fields));
    }
    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ApiError> constraint(ConstraintViolationException ex) { return ResponseEntity.badRequest().body(ApiError.of(400, "VALIDATION_ERROR", ex.getMessage())); }
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiError> integrity(DataIntegrityViolationException ex) { return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError.of(409, "DATA_INTEGRITY_ERROR", "The operation conflicts with existing data.")); }
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> unexpected(Exception ex) { return ResponseEntity.internalServerError().body(ApiError.of(500, "INTERNAL_ERROR", "An unexpected error occurred.")); }
}

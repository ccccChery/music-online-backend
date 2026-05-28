package com.sqa.musiconline.common.exception;

import com.sqa.musiconline.common.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                          HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Request validation failed.");
        log.warn("Validation failed. method={}, uri={}, userIdHeader={}, message={}",
                request.getMethod(), request.getRequestURI(), request.getHeader("X-User-Id"), message);
        return ResponseEntity.badRequest().body(ApiResponse.error(message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex,
                                                                       HttpServletRequest request) {
        log.warn("Constraint violation. method={}, uri={}, userIdHeader={}, message={}",
                request.getMethod(), request.getRequestURI(), request.getHeader("X-User-Id"), ex.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex,
                                                                   HttpServletRequest request) {
        log.warn("Request rejected. method={}, uri={}, userIdHeader={}, message={}",
                request.getMethod(), request.getRequestURI(), request.getHeader("X-User-Id"), ex.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(CartStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleCartState(CartStateException ex,
                                                             HttpServletRequest request) {
        log.error("Cart state error. method={}, uri={}, userIdHeader={}, message={}",
                request.getMethod(), request.getRequestURI(), request.getHeader("X-User-Id"), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception ex,
                                                                       HttpServletRequest request) {
        log.error("Unexpected server error. method={}, uri={}, userIdHeader={}, origin={}",
                request.getMethod(),
                request.getRequestURI(),
                request.getHeader("X-User-Id"),
                request.getHeader("Origin"),
                ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Unexpected server error."));
    }
}

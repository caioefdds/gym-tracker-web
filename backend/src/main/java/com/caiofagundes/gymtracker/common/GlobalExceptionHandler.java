/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.validation.ConstraintViolationException
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.HttpStatusCode
 *  org.springframework.http.ResponseEntity
 *  org.springframework.web.bind.MethodArgumentNotValidException
 *  org.springframework.web.bind.annotation.ExceptionHandler
 *  org.springframework.web.bind.annotation.RestControllerAdvice
 */
package com.caiofagundes.gymtracker.common;

import com.caiofagundes.gymtracker.common.ConflictException;
import com.caiofagundes.gymtracker.common.NotFoundException;
import com.caiofagundes.gymtracker.common.UnauthorizedException;
import jakarta.validation.ConstraintViolationException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value={NotFoundException.class})
    public ResponseEntity<ErrorResponse> notFound(NotFoundException ex) {
        return this.error(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(value={ConflictException.class})
    public ResponseEntity<ErrorResponse> conflict(ConflictException ex) {
        return this.error(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    @ExceptionHandler(value={UnauthorizedException.class})
    public ResponseEntity<ErrorResponse> unauthorized(UnauthorizedException ex) {
        return this.error(HttpStatus.UNAUTHORIZED, ex.getMessage(), null);
    }

    @ExceptionHandler(value={MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        HashMap<String, String> fields = new HashMap<String, String>();
        ex.getBindingResult().getFieldErrors().forEach(fe -> fields.put(fe.getField(), fe.getDefaultMessage()));
        return this.error(HttpStatus.BAD_REQUEST, "Dados inv\u00e1lidos", fields);
    }

    @ExceptionHandler(value={ConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> handleViolation(ConstraintViolationException ex) {
        return this.error(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(value={IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
        return this.error(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(value={Exception.class})
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        return this.error(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possível concluir a operação", null);
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, String message, Map<String, String> fields) {
        return ResponseEntity.status(status).body(new ErrorResponse(status.value(), message, OffsetDateTime.now(), fields));
    }

    public record ErrorResponse(int status, String message, OffsetDateTime timestamp, Map<String, String> fieldErrors) {
    }
}


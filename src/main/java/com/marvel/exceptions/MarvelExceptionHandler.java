package com.marvel.exceptions;

import com.marvel.record.ErrorCode;
import com.marvel.record.ErrorRecord;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class MarvelExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMissingPathVariable(@NonNull MissingPathVariableException ex,
                                                               @NonNull HttpHeaders headers,
                                                               @NonNull HttpStatus status,
                                                               @NonNull WebRequest request) {
        return new ResponseEntity<>(new ErrorRecord(ErrorCode.MISSING_PATH_PARAMETER, ex.getVariableName(), ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<List<ErrorRecord>> constraintViolationExceptionHandler(ConstraintViolationException ex) {
        List<ErrorRecord> errorRecords = ex.getConstraintViolations().stream()
                .map(fieldError -> new ErrorRecord(ErrorCode.FIELD_NOT_VALID, fieldError.getPropertyPath().toString(), fieldError.getMessage()))
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errorRecords);
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<Void> notFoundExceptionHandler(NotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(value = IncorrectFieldException.class)
    public ResponseEntity<ErrorRecord> incorrectFieldExceptionHandler(IncorrectFieldException ex) {
        return ResponseEntity.badRequest().body(new ErrorRecord(ErrorCode.FIELD_NOT_VALID, ex.getField(), ex.getMessage()));
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatus status,
                                                                  @NonNull WebRequest request) {
        List<ErrorRecord> errorRecords = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ErrorRecord(ErrorCode.FIELD_NOT_VALID, fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errorRecords);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleBindException(@NonNull BindException ex,
                                                         @NonNull HttpHeaders headers,
                                                         @NonNull HttpStatus status,
                                                         @NonNull WebRequest request) {
        List<ErrorRecord> errorRecords = ex.getFieldErrors().stream()
                .map(fieldError -> new ErrorRecord(ErrorCode.FIELD_NOT_VALID, fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errorRecords);
    }

}

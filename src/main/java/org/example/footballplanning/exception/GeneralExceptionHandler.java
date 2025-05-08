package org.example.footballplanning.exception;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.exception.customExceptions.*;
import org.example.footballplanning.util.MessageUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class GeneralExceptionHandler {

    MessageUtil messageUtil;

    @ExceptionHandler({
            ConflictException.class,
            DebtException.class,
            FileStorageException.class,
            GameValidationException.class,
            ObjectAlreadyExistsException.class,
            PasswordException.class,
            PhotoOperationException.class,
            SendRequestException.class,
            SessionExpiredException.class,
            TeamException.class,
            ValidationException.class
    })
    public ResponseEntity<ErrorResponseBean> handleBadRequestExceptions(RuntimeException ex) {
        ErrorResponseBean response = ErrorResponseBean.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .data(ex)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ErrorResponseBean> handleNotFoundException(ObjectNotFoundException ex) {
        ErrorResponseBean response = ErrorResponseBean.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(ex.getMessage())
                .data(ex)
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseBean> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(f -> f.getField() + " " + messageUtil.getMessage(f.getDefaultMessage()))
                .collect(Collectors.toList());
        ErrorResponseBean response = ErrorResponseBean.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .data(errors)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseBean> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        ErrorResponseBean response = ErrorResponseBean.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .data(ex)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseBean> handleGeneralException(Exception ex) {
        ErrorResponseBean response = ErrorResponseBean.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(ex.getMessage())
                .data(ex)
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
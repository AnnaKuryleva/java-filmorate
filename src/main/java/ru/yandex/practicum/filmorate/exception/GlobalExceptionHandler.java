package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Глобальный обработчик исключений для приложения. Обрабатывает различные типы исключений,
 * возникающих в процессе обработки HTTP-запросов.
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("status", HttpStatus.BAD_REQUEST.value());
        errors.put("error", "Validation Error");
        Map<String, String> fieldErrors = new HashMap<>();
        List<FieldError> fieldErrorList = e.getBindingResult().getFieldErrors();
        for (FieldError error : fieldErrorList) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
            log.error("Ошибка валидации поля {}: {}", error.getField(), error.getDefaultMessage(), e);
        }
        errors.put("messages", fieldErrors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Некорректный аргумент:", e);
        Map<String, Object> errors = new HashMap<>();
        errors.put("status", HttpStatus.BAD_REQUEST.value());
        errors.put("error", "Invalid Argument");
        errors.put("message", e.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(NotFoundException e) {
        log.error("Ресурс не найден: {}", e.getMessage(), e);
        Map<String, Object> errors = new HashMap<>();
        errors.put("status", HttpStatus.NOT_FOUND.value());
        errors.put("error", "Not Found");
        errors.put("message", e.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception e) {
        log.error("Непредвиденная ошибка: {}", e.getMessage(), e);
        Map<String, Object> errors = new HashMap<>();
        errors.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errors.put("error", "Internal Server Error" + e.getMessage());
        errors.put("message", "Произошла непредвиденная ошибка");
        return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotAllowedException(HttpRequestMethodNotSupportedException e) {
        log.error("Неподдерживаемый метод {}:", e.getMessage(), e);
        Map<String, Object> errors = new HashMap<>();
        errors.put("status", HttpStatus.METHOD_NOT_ALLOWED.value());
        errors.put("error", "Method Not Allowed");
        errors.put("message", "Метод " + e.getMethod() + " не поддерживается для этого ресурса");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", String.join(", ", e.getSupportedMethods() != null ?
                e.getSupportedMethods() : new String[]{"GET", "POST", "PUT"}));
        return new ResponseEntity<>(errors, headers, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.error("Ресурс не найден по URL {}: {}", e.getRequestURL(), e.getMessage(), e);
        Map<String, Object> errors = new HashMap<>();
        errors.put("status", HttpStatus.NOT_FOUND.value());
        errors.put("error", "Not Found");
        errors.put("message", "Ресурс " + e.getRequestURL() + " не найден");
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

}
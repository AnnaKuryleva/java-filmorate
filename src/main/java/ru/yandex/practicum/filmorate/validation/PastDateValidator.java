package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Validator для проверки даты в прошлом.
 */

@Slf4j
public class PastDateValidator implements ConstraintValidator<PastDate, String> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        try {
            LocalDate date = LocalDate.parse(value, FORMATTER);
            return !date.isAfter(LocalDate.now());
        } catch (DateTimeParseException e) {
            log.warn("Некорректный формат даты: '{}', ожидается yyyy-MM-dd", value);
            return true;
        }
    }
}
package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Validator для проверки минимальной даты релиза.
 */

@Slf4j
public class MinReleaseDateValidator implements ConstraintValidator<MinReleaseDate, String> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        try {
            LocalDate date = LocalDate.parse(value, FORMATTER);
            return !date.isBefore(MIN_DATE);
        } catch (DateTimeParseException e) {
            log.warn("Некорректный формат даты: '{}', ожидается yyyy-MM-dd", value);
            return true;
        }
    }
}
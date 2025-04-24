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
    private LocalDate minDate;


    @Override
    public void initialize(MinReleaseDate constraintAnnotation) {
        try {
            minDate = LocalDate.parse(constraintAnnotation.value(), FORMATTER);
        } catch (DateTimeParseException e) {
            log.error("Некорректный формат даты в аннотации: '{}'", constraintAnnotation.value());
            throw new IllegalArgumentException("Дата в аннотации должна быть в формате yyyy-MM-dd");
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        try {
            LocalDate date = LocalDate.parse(value, FORMATTER);
            return !date.isBefore(minDate);
        } catch (DateTimeParseException e) {
            log.warn("Некорректный формат даты: '{}', ожидается yyyy-MM-dd", value);
            return true;
        }
    }
}
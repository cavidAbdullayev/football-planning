package org.example.footballplanning.util;

import org.example.footballplanning.exception.customExceptions.ValidationException;

public class ValidationUtil {

    public static void checkPageSizeAndNumber(Integer size, Integer page) {
        if (size <= 0) {
            throw new ValidationException("Invalid page size!");
        }

        if (page < 0) {
            throw new ValidationException("Invalid page number!");
        }
    }
}
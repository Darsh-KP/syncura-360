package com.syncura360.dto;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

/**
 * Utility class for converting validation errors from a {@link BindingResult} into a single string message.
 * The errors from all fields are concatenated into one string, separated by spaces.
 *
 * @author Darsh-KP
 */
public class ErrorConvertor {
    public static String convertErrorsToString(BindingResult bindingResult) {
        // Start a new string builder
        StringBuilder errors = new StringBuilder();

        // Combine all the errors into one string
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.append(error.getDefaultMessage())
                    .append(" ");
        }

        // Remove the last new line
        if (!errors.isEmpty()) {
            errors.deleteCharAt(errors.length() - 1);
        }

        return errors.toString();
    }
}

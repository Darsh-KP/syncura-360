package com.syncura360.dto;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

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

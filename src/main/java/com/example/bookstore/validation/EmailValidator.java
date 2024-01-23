package com.example.bookstore.validation;

import com.example.bookstore.config.Config;
import com.example.bookstore.validation.anotations.Email;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<Email, String> {
    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        return email != null && Pattern.compile(Config.PATTERN_OF_EMAIL).matcher(email).matches();
    }
}

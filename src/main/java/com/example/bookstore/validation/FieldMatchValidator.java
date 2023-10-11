package com.example.bookstore.validation;

import com.example.bookstore.exception.RegistrationException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public final class FieldMatchValidator implements ConstraintValidator <FieldMatch, Object>{
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        final Object firstObject = getValue(value, firstFieldName);
        final Object secondtObject = getValue(value, secondFieldName);
        if (firstObject != null) {
            return firstObject.equals(secondtObject);
        } else return secondtObject == null;
    }

    private Object getValue(Object value, String fieldName) {
        try {
            final Object Object = org.springframework.beans.BeanUtils.getPropertyDescriptor(value.getClass(), fieldName)
                    .getReadMethod().invoke(value);
            return Object;
        } catch (final Exception e) {
            throw new RuntimeException("Something wrong with getting field value", e);
        }
    }
}

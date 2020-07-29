package cn.kinkii.novice.framework.controller.validation;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public abstract class AbstractOneOfValidator<T> implements ConstraintValidator<OneOf, T> {

    private static final String VALUES_PARAM = "values";

    protected String[] availableValues;

    protected String message;

    @Override
    public void initialize(OneOf annotation) {
        this.availableValues = annotation.value();
        this.message = annotation.message();
    }

    @Override
    public boolean isValid(T value, ConstraintValidatorContext ctx) {
        // null values are valid
        if (value == null) {
            return true;
        }

        for (String availableValue : availableValues) {
            if (compare(value, availableValue)) {
                return true;
            }
        }

        return false;
    }

    protected abstract boolean compare(T value, String availableValue);

}

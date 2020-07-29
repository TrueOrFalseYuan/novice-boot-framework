package cn.kinkii.novice.framework.controller.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public abstract class AbstractOneOfValidator<T> implements ConstraintValidator<OneOf, T> {

    protected String[] availableValues;

    @Override
    public void initialize(OneOf annotation) {
        this.availableValues = annotation.value();
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

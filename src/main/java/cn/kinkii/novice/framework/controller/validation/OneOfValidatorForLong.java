package cn.kinkii.novice.framework.controller.validation;

public class OneOfValidatorForLong extends AbstractOneOfValidator<Long> {

    @Override
    protected boolean compare(Long value, String availableValue) {
        try {
            return Long.parseLong(availableValue) == value;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}

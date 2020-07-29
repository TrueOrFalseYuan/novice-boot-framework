package cn.kinkii.novice.framework.controller.validation;

public class OneOfValidatorForInteger extends AbstractOneOfValidator<Integer> {

    @Override
    protected boolean compare(Integer value, String availableValue) {
        try {
            return Integer.parseInt(availableValue) == value;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}

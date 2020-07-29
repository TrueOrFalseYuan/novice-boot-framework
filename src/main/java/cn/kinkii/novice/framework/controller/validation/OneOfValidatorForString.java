package cn.kinkii.novice.framework.controller.validation;

public class OneOfValidatorForString extends AbstractOneOfValidator<String> {

    @Override
    protected boolean compare(String value, String availableValue) {
        return availableValue.equals(value);
    }

}

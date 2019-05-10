package cn.kinkii.novice.framework.controller.exception;

public class InvalidParamException extends ControllerException {

    public static final Integer INVALID_PARAM_EXCEPTION_CODE = -404;

    public InvalidParamException(String message) {
        super(INVALID_PARAM_EXCEPTION_CODE, message);
    }
}

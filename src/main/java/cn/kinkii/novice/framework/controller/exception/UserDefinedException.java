package cn.kinkii.novice.framework.controller.exception;

public class UserDefinedException extends ControllerException {

    public UserDefinedException(Integer code) {
        super(code);
    }

    public UserDefinedException(Integer code, String message) {
        super(code, message);
    }
}

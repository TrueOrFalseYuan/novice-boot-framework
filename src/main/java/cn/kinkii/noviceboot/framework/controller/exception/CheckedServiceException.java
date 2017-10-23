package cn.kinkii.noviceboot.framework.controller.exception;

public class CheckedServiceException extends ControllerException {

    public CheckedServiceException(Integer code) {
        super(code);
    }
}

package cn.kinkii.novice.framework.controller.exception;


import cn.kinkii.novice.framework.exception.BaseException;

public class ControllerException extends BaseException {

    public ControllerException(Integer code) {
        super(code);
    }

    public ControllerException(Integer code, String message) {
        super(code, message);
    }

}

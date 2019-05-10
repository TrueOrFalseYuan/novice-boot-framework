package cn.kinkii.novice.framework.controller.exception;

import cn.kinkii.novice.framework.controller.GlobalExceptionCode;

public class InvalidDataException extends ControllerException {

    public InvalidDataException(String message) {
        super(GlobalExceptionCode.INVALID_DATA_EXCEPTION_CODE, message);
    }

}

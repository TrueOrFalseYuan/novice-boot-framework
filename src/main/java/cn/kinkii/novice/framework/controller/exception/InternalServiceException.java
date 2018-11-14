package cn.kinkii.novice.framework.controller.exception;

import cn.kinkii.novice.framework.controller.GlobalExceptionCode;

public class InternalServiceException extends ControllerException {

    public InternalServiceException(String message) {
        super(GlobalExceptionCode.INTERNAL_SERVICE_EXCEPTION_CODE, message);
    }

}

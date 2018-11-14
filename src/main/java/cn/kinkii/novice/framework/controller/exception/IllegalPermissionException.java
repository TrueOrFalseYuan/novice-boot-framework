package cn.kinkii.novice.framework.controller.exception;

import cn.kinkii.novice.framework.controller.GlobalExceptionCode;

public class IllegalPermissionException extends ControllerException {

    public IllegalPermissionException(String message) {
        super(GlobalExceptionCode.ILLEGAL_PERMISSION_EXCEPTION_CODE, message);
    }

}

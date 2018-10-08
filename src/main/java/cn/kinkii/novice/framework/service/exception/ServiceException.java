package cn.kinkii.novice.framework.service.exception;

import cn.kinkii.novice.framework.exception.BaseException;

public class ServiceException extends BaseException {

    public ServiceException(Integer code) {
        super(code);
    }

    public ServiceException(Integer code, String message) {
        super(code, message);
    }

}

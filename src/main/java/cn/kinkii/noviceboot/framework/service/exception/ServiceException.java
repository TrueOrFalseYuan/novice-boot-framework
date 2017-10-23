package cn.kinkii.noviceboot.framework.service.exception;

import lombok.Getter;
import lombok.Setter;

public class ServiceException extends Exception {

    @Getter
    @Setter
    protected Integer code;

    public ServiceException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}

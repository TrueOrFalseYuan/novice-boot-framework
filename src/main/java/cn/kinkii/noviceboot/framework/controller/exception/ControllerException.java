package cn.kinkii.noviceboot.framework.controller.exception;


import lombok.Getter;
import lombok.Setter;

public class ControllerException extends RuntimeException {

    @Getter
    @Setter
    protected Integer code;

    public ControllerException(Integer code) {
        this.code = code;
    }

    public ControllerException(Integer code, String message) {
        super(message);
        this.code = code;
    }

}

package cn.kinkii.novice.framework.controller;

import cn.kinkii.novice.framework.controller.exception.IllegalPermissionException;
import cn.kinkii.novice.framework.controller.exception.InternalServiceException;
import cn.kinkii.novice.framework.controller.exception.InvalidDataException;
import cn.kinkii.novice.framework.controller.exception.InvalidParamException;
import cn.kinkii.novice.framework.exception.BaseException;
import cn.kinkii.novice.framework.service.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Locale;

@Component
@RestControllerAdvice
@Slf4j
public class BaseControllerHandler {


    private static final String EXCEPTION_DETAIL = "detail";

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(InternalServiceException exception) {
        log.error("internal error:" + exception.getMessage(), exception);
        return buildResult(exception, GlobalMessage.ERROR_SERVICE);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(InvalidParamException exception) {
        log.error("invalid params error:" + exception.getMessage(), exception);
        return buildResult(exception, GlobalMessage.ERROR_PARAMETER);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(InvalidDataException exception) {
        log.error("invalid data error:" + exception.getMessage(), exception);
        return buildResult(exception, GlobalMessage.ERROR_DATA);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(IllegalPermissionException exception) {
        log.error("illegal permission error:" + exception.getMessage(), exception);
        return buildResult(exception, GlobalMessage.ERROR_PERMISSION);
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(ServiceException exception) {
        log.error("checked service error:" + exception.getMessage(), exception);
        return buildResult(exception, GlobalMessage.ERROR_SERVICE);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(DataAccessException exception) {
        log.error("data access error:" + exception.getMessage(), exception);
        return BaseResult.failure(GlobalExceptionCode.DATA_ACCESS_EXCEPTION_CODE, GlobalMessage.ERROR_DATA.getMessageKey());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(ConstraintViolationException exception) {
        log.error("constraint violation error:" + exception.getMessage(), exception);
        BaseResult baseResult = BaseResult.failure(GlobalExceptionCode.DATA_ACCESS_EXCEPTION_CODE, GlobalMessage.ERROR_DATA.getMessageKey());
        exception.getConstraintViolations().forEach(constraintViolation -> {
            baseResult.addValue(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
        });
        return baseResult;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(MethodArgumentNotValidException exception) {
        log.error("method args not valid error:" + exception.getMessage(), exception);
        BaseResult baseResult = BaseResult.failure(GlobalExceptionCode.DATA_ACCESS_EXCEPTION_CODE, GlobalMessage.ERROR_DATA.getMessageKey());
        exception.getBindingResult().getFieldErrors().forEach(fieldError -> {
            baseResult.addValue(fieldError.getField(), fieldError.getDefaultMessage());
        });
        return baseResult;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(HttpMessageNotReadableException exception) {
        log.error("http message not readable error:" + exception.getMessage(), exception);
        return buildResult(GlobalExceptionCode.HTTP_MESSAGE_NOT_READABLE_CODE, exception, GlobalMessage.ERROR_DATA);
    }


    private String getMessage(String messageCode) {
        return getMessage(messageCode, null);
    }

    private String getMessage(String messageCode, Object[] params) {
        Locale currentLocal = LocaleContextHolder.getLocale();
        try {
            return messageSource.getMessage(messageCode, params, currentLocal);
        } catch (NoSuchMessageException e) {
            return messageCode;
        }
    }

    private BaseResult buildResult(BaseException e, GlobalMessage message) {
        if (StringUtils.hasText(e.getMessage())) {
            return BaseResult.failure(e.getCode(), getMessage(message.getMessageKey())).addValue(EXCEPTION_DETAIL, e.getMessage());
        } else {
            return BaseResult.failure(e.getCode(), getMessage(message.getMessageKey()));
        }
    }

    private BaseResult buildResult(Integer code, Exception e, GlobalMessage message) {
        if (StringUtils.hasText(e.getMessage())) {
            return BaseResult.failure(code, getMessage(message.getMessageKey())).addValue(EXCEPTION_DETAIL, e.getMessage());
        } else {
            return BaseResult.failure(code, getMessage(message.getMessageKey()));
        }
    }

}

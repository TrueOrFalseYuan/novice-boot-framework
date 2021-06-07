package cn.kinkii.novice.framework.controller;

import cn.kinkii.novice.framework.controller.exception.*;
import cn.kinkii.novice.framework.exception.BaseException;
import cn.kinkii.novice.framework.service.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@Component
@RestControllerAdvice
@Slf4j
public class BaseControllerHandler {

    private static final String EXCEPTION_DETAIL = "detail";
    private static final String EXCEPTION_RESPONSE_PREFIX = "defined.response.";

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(UserDefinedException ex) {
        log.error("user defined error:" + ex.getMessage(), ex);
        if (StringUtils.hasText(ex.getMessage())) {
            return BaseResult.failure(ex.getCode(), ex.getMessage());
        } else {
            return buildResult(ex.getCode(), ex, EXCEPTION_RESPONSE_PREFIX + ex.getCode(), GlobalMessage.ERROR_SERVICE.getMessageKey());
        }
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(InternalServiceException ex) {
        log.error("internal error:" + ex.getMessage(), ex);
        return buildResult(ex, GlobalMessage.ERROR_SERVICE);
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(InvalidParamException ex) {
        log.error("invalid params error:" + ex.getMessage(), ex);
        return buildResult(ex, GlobalMessage.ERROR_PARAMETER);
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(InvalidDataException ex) {
        log.error("invalid data error:" + ex.getMessage(), ex);
        return buildResult(ex, GlobalMessage.ERROR_DATA);
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(IllegalPermissionException ex) {
        log.error("illegal permission error:" + ex.getMessage(), ex);
        return buildResult(ex, GlobalMessage.ERROR_PERMISSION);
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(ServiceException ex) {
        log.error("checked service error:" + ex.getMessage(), ex);
        return buildResult(ex.getCode(), ex, EXCEPTION_RESPONSE_PREFIX + ex.getCode(), GlobalMessage.ERROR_SERVICE.getMessageKey());
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(DataAccessException ex) {
        log.error("data access error:" + ex.getMessage(), ex);
        return buildResult(GlobalExceptionCode.INVALID_DATA_EXCEPTION_CODE, ex, GlobalMessage.ERROR_DATA);
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(DataIntegrityViolationException ex) {
        log.error("constraint violation error:" + ex.getMessage(), ex);
        if (ex.getCause() instanceof ConstraintViolationException) {
            return buildResult(GlobalExceptionCode.INVALID_DATA_EXCEPTION_CODE, ex, GlobalMessage.ERROR_DATA_BOUND);
        } else {
            return buildResult(GlobalExceptionCode.INVALID_DATA_EXCEPTION_CODE, ex, GlobalMessage.ERROR_DATA);
        }
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(BindException ex) {
        log.error("invalid params error:" + ex.getMessage(), ex);
        BaseResult baseResult = buildResult(GlobalExceptionCode.INVALID_PARAM_EXCEPTION_CODE, ex, GlobalMessage.ERROR_PARAMETER);
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            baseResult.addValue(fieldError.getField(), fieldError.getDefaultMessage());
        });
        return baseResult;
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(MethodArgumentNotValidException ex) {
        log.error("method args not valid error:" + ex.getMessage(), ex);
        BaseResult baseResult = buildResult(GlobalExceptionCode.INVALID_PARAM_EXCEPTION_CODE, ex, GlobalMessage.ERROR_PARAMETER);
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            baseResult.addValue(fieldError.getField(), fieldError.getDefaultMessage());
        });
        return baseResult;
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(MissingServletRequestParameterException ex) {
        log.error("missing request parameter mismatch:" + ex.getMessage(), ex);
        return buildResult(GlobalExceptionCode.INVALID_PARAM_EXCEPTION_CODE, ex, GlobalMessage.ERROR_PARAMETER);
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(HttpMessageNotReadableException ex) {
        log.error("http message not readable:" + ex.getMessage(), ex);
        return buildResult(GlobalExceptionCode.BAD_REQUEST_EXCEPTION_CODE, ex, GlobalMessage.ERROR_REQUEST);
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(TypeMismatchException ex) {
        log.error("type mismatch:" + ex.getMessage(), ex);
        return buildResult(GlobalExceptionCode.BAD_REQUEST_EXCEPTION_CODE, ex, GlobalMessage.ERROR_REQUEST);
    }

    private String getMessage(String messageCode) {
        return getMessage(messageCode, null);
    }

    private String getMessage(String messageCode, String defaultMessageCode) {
        //set use-code-as-default-message to false
        Locale currentLocal = LocaleContextHolder.getLocale();
        String message = null;
        try {
            message = messageSource.getMessage(messageCode, null, currentLocal);
        } catch (NoSuchMessageException ignored) {
        }
        if (message == null && defaultMessageCode != null) {
            try {
                message = messageSource.getMessage(defaultMessageCode, null, currentLocal);
            } catch (NoSuchMessageException ignored) {
            }
        }
        if (message == null) {
            message = messageCode;
        }
        return message;
    }


    private BaseResult buildResult(BaseException cause, GlobalMessage message) {
        return buildResult(cause.getCode(), cause, message);
    }

    private BaseResult buildResult(Integer code, Exception cause, GlobalMessage message) {
        return buildResult(code, cause, message.getMessageKey(), null);
    }

    private BaseResult buildResult(Integer code, Exception e, String messageKey, String defaultMessageKey) {
        if (StringUtils.hasText(e.getMessage())) {
            return BaseResult.failure(code, getMessage(messageKey, defaultMessageKey)).addValue(EXCEPTION_DETAIL, e.getMessage());
        } else {
            return BaseResult.failure(code, getMessage(messageKey, defaultMessageKey));
        }
    }
}

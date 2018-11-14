package cn.kinkii.novice.framework.controller;

import cn.kinkii.novice.framework.controller.exception.IllegalPermissionException;
import cn.kinkii.novice.framework.controller.exception.InternalServiceException;
import cn.kinkii.novice.framework.controller.exception.InvalidDataException;
import cn.kinkii.novice.framework.controller.exception.InvalidParamException;
import cn.kinkii.novice.framework.exception.BaseException;
import cn.kinkii.novice.framework.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataAccessException;
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

import javax.validation.ConstraintViolationException;
import java.util.Locale;

@Component
@RestControllerAdvice
public class BaseControllerHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseControllerHandler.class);

    private static final String EXCEPTION_DETAIL = "detail";
    private static final String EXCEPTION_SERVICE_ERROR_PREFIX = "service.error.";

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(InternalServiceException ex) {
        LOGGER.error("internal error:" + ex.getMessage(), ex);
        return buildResult(ex, GlobalMessage.ERROR_SERVICE);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(InvalidParamException ex) {
        LOGGER.error("invalid params error:" + ex.getMessage(), ex);
        return buildResult(ex, GlobalMessage.ERROR_PARAMETER);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(InvalidDataException ex) {
        LOGGER.error("invalid data error:" + ex.getMessage(), ex);
        return buildResult(ex, GlobalMessage.ERROR_DATA);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(IllegalPermissionException ex) {
        LOGGER.error("illegal permission error:" + ex.getMessage(), ex);
        return buildResult(ex, GlobalMessage.ERROR_PERMISSION);
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(ServiceException ex) {
        LOGGER.error("checked service error:" + ex.getMessage(), ex);
        return buildResult(ex.getCode(), ex, EXCEPTION_SERVICE_ERROR_PREFIX + ex.getCode(), GlobalMessage.ERROR_SERVICE.getMessageKey());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(DataAccessException ex) {
        LOGGER.error("data access error:" + ex.getMessage(), ex);
        return buildResult(GlobalExceptionCode.INVALID_DATA_EXCEPTION_CODE, ex, GlobalMessage.ERROR_DATA);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(ConstraintViolationException ex) {
        LOGGER.error("constraint violation error:" + ex.getMessage(), ex);
        BaseResult baseResult = buildResult(GlobalExceptionCode.INVALID_DATA_EXCEPTION_CODE, ex, GlobalMessage.ERROR_DATA);
        ex.getConstraintViolations().forEach(constraintViolation -> {
            baseResult.addValue(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
        });
        return baseResult;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(BindException ex) {
        LOGGER.error("invalid params error:" + ex.getMessage(), ex);
        BaseResult baseResult = buildResult(GlobalExceptionCode.INVALID_PARAM_EXCEPTION_CODE, ex, GlobalMessage.ERROR_PARAMETER);
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            baseResult.addValue(fieldError.getField(), fieldError.getDefaultMessage());
        });
        return baseResult;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(MethodArgumentNotValidException ex) {
        LOGGER.error("method args not valid error:" + ex.getMessage(), ex);
        BaseResult baseResult = buildResult(GlobalExceptionCode.INVALID_PARAM_EXCEPTION_CODE, ex, GlobalMessage.ERROR_PARAMETER);
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            baseResult.addValue(fieldError.getField(), fieldError.getDefaultMessage());
        });
        return baseResult;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(MissingServletRequestParameterException ex) {
        LOGGER.error("missing request parameter mismatch:" + ex.getMessage(), ex);
        return buildResult(GlobalExceptionCode.INVALID_PARAM_EXCEPTION_CODE, ex, GlobalMessage.ERROR_PARAMETER);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(HttpMessageNotReadableException ex) {
        LOGGER.error("http message not readable:" + ex.getMessage(), ex);
        return buildResult(GlobalExceptionCode.BAD_REQUEST_EXCEPTION_CODE, ex, GlobalMessage.ERROR_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(TypeMismatchException ex) {
        LOGGER.error("type mismatch:" + ex.getMessage(), ex);
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

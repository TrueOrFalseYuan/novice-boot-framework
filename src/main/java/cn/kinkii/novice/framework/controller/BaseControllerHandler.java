package cn.kinkii.novice.framework.controller;

import cn.kinkii.novice.framework.controller.exception.IllegalPermissionException;
import cn.kinkii.novice.framework.controller.exception.InternalServiceException;
import cn.kinkii.novice.framework.controller.exception.InvalidDataException;
import cn.kinkii.novice.framework.controller.exception.InvalidParamException;
import cn.kinkii.novice.framework.exception.BaseException;
import cn.kinkii.novice.framework.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class BaseControllerHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseControllerHandler.class);

    private static final String EXCEPTION_DETAIL = "detail";
    private static final String EXCEPTION_SERVICE_ERROR_PREFIX = "service.error.";

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(InternalServiceException exception) {
        LOGGER.error("internal error:" + exception.getMessage(), exception);
        return buildResult(exception, GlobalMessage.ERROR_SERVICE);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(InvalidParamException exception) {
        LOGGER.error("invalid params error:" + exception.getMessage(), exception);
        return buildResult(exception, GlobalMessage.ERROR_PARAMETER);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(InvalidDataException exception) {
        LOGGER.error("invalid data error:" + exception.getMessage(), exception);
        return buildResult(exception, GlobalMessage.ERROR_DATA);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(IllegalPermissionException exception) {
        LOGGER.error("illegal permission error:" + exception.getMessage(), exception);
        return buildResult(exception, GlobalMessage.ERROR_PERMISSION);
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(ServiceException exception) {
        LOGGER.error("checked service error:" + exception.getMessage(), exception);
        return buildResult(exception.getCode(), exception, EXCEPTION_SERVICE_ERROR_PREFIX + exception.getCode(), GlobalMessage.ERROR_SERVICE.getMessageKey());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(DataAccessException exception) {
        LOGGER.error("data access error:" + exception.getMessage(), exception);
        return BaseResult.failure(GlobalExceptionCode.DATA_ACCESS_EXCEPTION_CODE, GlobalMessage.ERROR_DATA.getMessageKey());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(ConstraintViolationException exception) {
        LOGGER.error("constraint violation error:" + exception.getMessage(), exception);
        BaseResult baseResult = BaseResult.failure(GlobalExceptionCode.DATA_ACCESS_EXCEPTION_CODE, GlobalMessage.ERROR_DATA.getMessageKey());
        exception.getConstraintViolations().forEach(constraintViolation -> {
            baseResult.addValue(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
        });
        return baseResult;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(MethodArgumentNotValidException exception) {
        LOGGER.error("method args not valid error:" + exception.getMessage(), exception);
        BaseResult baseResult = BaseResult.failure(GlobalExceptionCode.DATA_ACCESS_EXCEPTION_CODE, GlobalMessage.ERROR_DATA.getMessageKey());
        exception.getBindingResult().getFieldErrors().forEach(fieldError -> {
            baseResult.addValue(fieldError.getField(), fieldError.getDefaultMessage());
        });
        return baseResult;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public BaseResult handle(HttpMessageNotReadableException exception) {
        LOGGER.error("http message not readable error:" + exception.getMessage(), exception);
        return buildResult(GlobalExceptionCode.HTTP_MESSAGE_NOT_READABLE_CODE, exception, GlobalMessage.ERROR_DATA);
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

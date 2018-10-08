package cn.kinkii.novice.framework.controller;

import cn.kinkii.novice.framework.controller.exception.InvalidParamException;
import cn.kinkii.novice.framework.controller.exception.InvalidParamException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

public abstract class BaseController {

    protected Logger logger;

    @Autowired
    protected MessageSource messageSource;

    public BaseController() {
        logger = LoggerFactory.getLogger(getClass());
    }

    /**
     * 非空属性检查
     *
     * @param values 字段值
     * @throws InvalidParamException
     */
    protected void checkNotBlank(String... values) throws InvalidParamException {
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                throw new InvalidParamException(getMessage(GlobalMessage.ERROR_PARAMETER.getMessageKey()));
            }
        }
    }

    /**
     * 非空属性检查
     *
     * @param values 字段值
     * @throws InvalidParamException
     */
    protected void checkNotNull(Object... values) throws InvalidParamException {
        for (Object value : values) {
            if (value == null) {
                throw new InvalidParamException(getMessage(GlobalMessage.ERROR_PARAMETER.getMessageKey()));
            }
        }
    }


    protected String getMessage(String messageCode) {
        return getMessage(messageCode, null);
    }

    protected String getMessage(String messageCode, Object[] params) {
        Locale currentLocal = LocaleContextHolder.getLocale();
        try {
            return messageSource.getMessage(messageCode, params, currentLocal);
        } catch (NoSuchMessageException e) {
            return messageCode;
        }
    }

}

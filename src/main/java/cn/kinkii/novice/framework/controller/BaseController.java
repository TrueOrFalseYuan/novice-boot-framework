package cn.kinkii.novice.framework.controller;

import cn.kinkii.novice.framework.entity.Identifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

public abstract class BaseController {

    protected Logger logger;

    @Autowired
    protected MessageSource messageSource;

    public BaseController() {
        logger = LoggerFactory.getLogger(getClass());
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

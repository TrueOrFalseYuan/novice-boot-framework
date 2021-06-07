package cn.kinkii.novice.framework.controller.i18n;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.AbstractLocaleContextResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class KLocaleContextResolver extends AbstractLocaleContextResolver {

    private final KLocaleConfig localeConfig;

    public KLocaleContextResolver(KLocaleConfig localeConfig) {
        this.localeConfig = localeConfig;
    }

    @Override
    public LocaleContext resolveLocaleContext(HttpServletRequest request) {
        return () -> {
            String localeValue = request.getParameter(localeConfig.getLocaleParam());
            Locale locale = localeConfig.getDefaultLocale();
            if (StringUtils.hasText(localeValue)) {
                locale = StringUtils.parseLocale(localeValue);
            }
            return locale;
        };
    }

    @Override
    public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext) {
        LocaleContextHolder.setLocaleContext(resolveLocaleContext(request));
    }
}

package cn.kinkii.novice.framework.controller.i18n;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Locale;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = KLocaleConfig.CONFIG_PREFIX)
public class KLocaleConfig {

    public static final String CONFIG_PREFIX = "novice.locale";

    public static final String DEFAULT_LOCALE_PARAM = "_locale";

    private String defaultLocale = Locale.getDefault().toString();

    private String localeParam = DEFAULT_LOCALE_PARAM;

    public Locale getDefaultLocale() {
        Locale locale = Locale.getDefault();
        if (StringUtils.hasText(defaultLocale)) {
            locale = StringUtils.parseLocale(defaultLocale);
        }
        return locale;
    }

}

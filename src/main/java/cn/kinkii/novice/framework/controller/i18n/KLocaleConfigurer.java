package cn.kinkii.novice.framework.controller.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@SuppressWarnings("NullableProblems")
@Configuration
public class KLocaleConfigurer {

    @Autowired
    private KLocaleConfig localeConfig;

    @Bean
    public LocaleResolver localeResolver() {
        return new KLocaleContextResolver(localeConfig);
    }

    @Bean
    public WebMvcConfigurer localeInterceptor() {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                LocaleChangeInterceptor localeInterceptor = new LocaleChangeInterceptor();
                localeInterceptor.setParamName(localeConfig.getLocaleParam());
                registry.addInterceptor(localeInterceptor);
            }
        };
    }

}

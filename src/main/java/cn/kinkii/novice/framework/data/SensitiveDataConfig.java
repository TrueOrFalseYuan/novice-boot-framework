package cn.kinkii.novice.framework.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
public abstract class SensitiveDataConfig implements WebMvcConfigurer {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                ((MappingJackson2HttpMessageConverter) converter).getObjectMapper()
                        .setAnnotationIntrospector(new SensitiveDataIntrospector(this.buildSensitiveStateChecks()));
            }
        }
    }

    protected abstract List<SensitiveStateChecker> buildSensitiveStateChecks();

}

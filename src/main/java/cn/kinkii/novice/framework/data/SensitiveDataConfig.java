package cn.kinkii.novice.framework.data;

import cn.kinkii.novice.framework.data.ser.SensitiveTypeSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
public abstract class SensitiveDataConfig implements WebMvcConfigurer {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (int i = 0; i < converters.size(); i++) {
            if (converters.get(i) instanceof MappingJackson2HttpMessageConverter) {
                ObjectMapper objectMapper = ((MappingJackson2HttpMessageConverter) converters.get(i)).getObjectMapper().copy();
                SerializationConfig serializationConfig = objectMapper.getSerializationConfig();
                ContextAttributes attributes = serializationConfig.getAttributes()
                        .withSharedAttribute(SensitiveDataSerializer.SENSITIVE_STATE_CHECKERS_KEY, this.buildStateCheckers())
                        .withSharedAttribute(SensitiveDataSerializer.SENSITIVE_ADDITIONAL_SERIALIZERS_KEY, this.buildAdditionalSerializers());
                converters.set(i, new MappingJackson2HttpMessageConverter(objectMapper.setConfig(serializationConfig.with(attributes))));
            }
        }
    }

    protected abstract List<SensitiveStateChecker> buildStateCheckers();

    protected List<SensitiveTypeSerializer<?>> buildAdditionalSerializers() {
        return null;
    }
}

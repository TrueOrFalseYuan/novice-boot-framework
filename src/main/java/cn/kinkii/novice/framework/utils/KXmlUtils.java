package cn.kinkii.novice.framework.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KXmlUtils {

    private static Logger logger = LoggerFactory.getLogger(KXmlUtils.class);
    private static XmlMapper mapper = new XmlMapper();

    static {
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public static String toXml(Object object) {
        String json = null;
        try {
            json = mapper.writeValueAsString(object);
            logger.debug(object.getClass() + " - " + json);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    public static <T> T toObject(String xml, Class<T> clazz) {
        try {
            logger.debug(clazz + " - " + xml);
            return mapper.readValue(xml, clazz);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


    public static <T> List<T> toList(String xml, Class<T> clazz) {
        JavaType type = mapper.getTypeFactory().constructParametricType(List.class, clazz);
        logger.debug(clazz + " - " + xml);
        try {
            return mapper.readValue(xml, type);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static <K, V> Map<K, V> toObjectMap(String xml, Class<K> keyClass, Class<V> valueClass) {
        JavaType type = mapper.getTypeFactory().constructParametricType(HashMap.class, keyClass, valueClass);
        logger.debug(keyClass + "/" + valueClass + " - " + xml);
        try {
            return mapper.readValue(xml, type);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}

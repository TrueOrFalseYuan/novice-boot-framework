package cn.kinkii.novice.framework.controller.response;


import cn.kinkii.novice.framework.controller.response.annotations.ResponseProperty;
import cn.kinkii.novice.framework.utils.KReflectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class AnnotatedResponse {

  // response class,source class, response field, source field
  private static final Map<Class<?>, Map<Class<?>, Map<Field, Field>>> fieldMapperCache = new ConcurrentReferenceHashMap<>();
  // response class,source class, response field, source get method
  private static final Map<Class<?>, Map<Class<?>, Map<Field, Method>>> methodMapperCache = new ConcurrentReferenceHashMap<>();

  protected Class<?> responseClass;

  public AnnotatedResponse() {
    responseClass = getClass();
    fieldMapperCache.computeIfAbsent(responseClass, k -> new HashMap<>());
    methodMapperCache.computeIfAbsent(responseClass, k -> new HashMap<>());
  }

  private static Map<Field, Field> buildFieldMapper(Class<?> responseClass, Class<?> sourceClass) {
    Map<Field, Field> mapper = new HashMap<>();
    KReflectionUtils.doWithFields(responseClass, respField -> {
      ResponseProperty respProp = respField.getAnnotation(ResponseProperty.class);
      String propName = respField.getName();
      if (respProp != null && !respProp.sourceProperty().equals("")) {
        propName = respProp.sourceProperty();
      }
      if (respProp == null || respProp.sourceClass() == Object.class || respProp.sourceClass() == sourceClass) {
        Field srcField = KReflectionUtils.findField(sourceClass, propName);
        if (srcField != null) {
          mapper.put(respField, srcField);
        }
      }
    });
    return mapper;
  }

  protected static Map<Field, Method> buildMethodMapper(Class<?> responseClass, Class<?> sourceClass) {
    Map<Field, Method> mapper = new HashMap<>();
    KReflectionUtils.doWithFields(responseClass, respField -> {
      ResponseProperty respProp = respField.getAnnotation(ResponseProperty.class);
      String propName = respField.getName();
      if (respProp != null && !respProp.sourceProperty().equals("")) {
        propName = respProp.sourceProperty();
      }
      if (respProp == null || respProp.sourceClass() == Object.class || respProp.sourceClass() == sourceClass) {
        Method srcGetter = KReflectionUtils.findActualMethod(sourceClass, "get" + StringUtils.capitalize(propName), respField.getType());
        if (srcGetter != null) {
          mapper.put(respField, srcGetter);
        }
      }
    });
    return mapper;
  }

  public void from(Object... sources) {
    Map<Class<?>, Map<Field, Field>> fieldMapper = fieldMapperCache.get(responseClass);
    Map<Class<?>, Map<Field, Method>> methodMapper = methodMapperCache.get(responseClass);
    for (Object src : sources) {
      Map<Field, Field> responseFieldMapper = fieldMapper.get(src.getClass());
      if (responseFieldMapper == null) {
        responseFieldMapper = buildFieldMapper(responseClass, src.getClass());
        fieldMapper.put(src.getClass(), responseFieldMapper);
      }
      for (Field responseField : responseFieldMapper.keySet()) {
        Field sourceField = responseFieldMapper.get(responseField);

        responseField.setAccessible(true);
        sourceField.setAccessible(true);
        try {
          responseField.set(this, sourceField.get(src));
        } catch (IllegalAccessException e) {
          throw new IllegalArgumentException(
              String.format("The <%s> of <%s> can't be valued by the <%s> of <%s>!", responseField.getName(), responseClass.getCanonicalName(), sourceField.getName(),
                            src.getClass().getCanonicalName()));
        }
      }

      Map<Field, Method> responseMethodMapper = methodMapper.get(src.getClass());
      if (responseMethodMapper == null) {
        responseMethodMapper = buildMethodMapper(responseClass, src.getClass());
        methodMapper.put(src.getClass(), responseMethodMapper);
      }
      for (Field responseField : responseMethodMapper.keySet()) {
        Method sourceGetter = responseMethodMapper.get(responseField);

        responseField.setAccessible(true);
        try {
          responseField.set(this, sourceGetter.invoke(src));
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
          throw new IllegalArgumentException(
              String.format("The <%s> of <%s> can't be valued by the method <%s> of <%s>!", responseField.getName(), responseClass.getCanonicalName(), sourceGetter.getName(),
                            src.getClass().getCanonicalName()));
        }
      }
    }
  }

}

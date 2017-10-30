package cn.kinkii.noviceboot.framework.controller.response;

import cn.kinkii.noviceboot.framework.controller.response.annotations.ResponseClass;
import cn.kinkii.noviceboot.framework.controller.response.annotations.ResponseProperty;
import cn.kinkii.noviceboot.framework.utils.KReflectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public abstract class AnnotatedResponse extends BaseResponse {

  @Override
  protected List<Class<?>> buildSourceClasses() {
    List<Class<?>> sources = new ArrayList<>();
    if (responseClass.isAnnotationPresent(ResponseClass.class)) {
      sources = Arrays.asList(responseClass.getAnnotation(ResponseClass.class).sourceClasses());
    }
    return sources;
  }

  @Override
  protected Map<Class<?>, Map<Field, Method>> buildFieldMapper() {
    Map<Class<?>, Map<Field, Method>> mapper = new HashMap<>();
    for (Class<?> srcClz : sourceClasses) {
      mapper.put(srcClz, new HashMap<>());
    }

    KReflectionUtils.doWithFields(responseClass, f -> {
      if (f.isAnnotationPresent(ResponseProperty.class)) {
        ResponseProperty respProp = f.getAnnotation(ResponseProperty.class);
        validateSourceClass(respProp.sourceClass());

        String fieldName = ("".equals(respProp.sourceProperty()) ? f.getName() : respProp.sourceProperty());
        mapper.get(respProp.sourceClass()).put(f, getGetter(respProp.sourceClass(), fieldName, f.getType()));
      } else {
        String getterName = "get" + StringUtils.capitalize(f.getName());
        for (Class<?> srcClz : sourceClasses) {
          Method getter = KReflectionUtils.findActualMethod(srcClz, getterName, f.getType());
          if (getter != null) {
            mapper.get(srcClz).put(f, getter);
          }
        }
      }
    });
    return mapper;
  }

  @Override
  protected Map<Class<?>, Map<Method, Method>> buildMethodMapper() {
    Map<Class<?>, Map<Method, Method>> mapper = new HashMap<>();
    for (Class<?> srcClz : sourceClasses) {
      mapper.put(srcClz, new HashMap<>());
    }

    KReflectionUtils.doWithMethods(responseClass, m -> {
      ResponseProperty respProp = m.getAnnotation(ResponseProperty.class);
      validateSourceClass(respProp.sourceClass());
      String fieldName = buildFieldNameFromAnnotatedMethod(m);
      mapper.get(respProp.sourceClass()).put(m, getGetter(respProp.sourceClass(), fieldName));
    }, m -> m.isAnnotationPresent(ResponseProperty.class));

    return mapper;
  }

  private String buildFieldNameFromAnnotatedMethod(Method annotatedMethod) {
    ResponseProperty respProp = annotatedMethod.getAnnotation(ResponseProperty.class);
    if (StringUtils.isNotBlank(respProp.sourceProperty())) {
      return respProp.sourceProperty();
    }

    if (annotatedMethod.getName().startsWith("set")) {
      return annotatedMethod.getName().substring(3, 4).toLowerCase() + annotatedMethod.getName().substring(4);
    } else {
      return annotatedMethod.getName();
    }
  }

  private Method getGetter(Class<?> srcClz, String name) {
    return getGetter(srcClz, name, null);
  }

  private Method getGetter(Class<?> srcClz, String name, Class<?> type) {
    String getterName = "get" + StringUtils.capitalize(name);
    Method getter = KReflectionUtils.findActualMethod(srcClz, getterName, type);
    if (getter == null) {
      throw new IllegalStateException(
          String.format("Unknown property <%s> in the source class <%s>!", name, srcClz.getCanonicalName()));
    }

    return getter;
  }

  private void validateSourceClass(Class<?> srcClz) {
    if (!sourceClasses.contains(srcClz)) {
      throw new IllegalStateException(
          String.format("The source class <%s> should be added to the ResponseClass annotation!",
                        srcClz.getCanonicalName()
          ));
    }
  }

}

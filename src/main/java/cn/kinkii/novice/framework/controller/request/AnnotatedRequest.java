package cn.kinkii.novice.framework.controller.request;

import cn.kinkii.novice.framework.utils.KReflectionUtils;
import cn.kinkii.novice.framework.controller.request.annotations.RequestProperty;
import cn.kinkii.novice.framework.utils.KReflectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class AnnotatedRequest {

  private static final Logger logger = LoggerFactory.getLogger(AnnotatedRequest.class);

  // request class,target class,request class field ,target class field
  private static final Map<Class<?>, Map<Class<?>, Map<Field, Member>>> fieldMapperCache = new ConcurrentReferenceHashMap<>();

  private static final Map<Class<?>, Map<Class<?>, Map<Method, Member>>> methodMapperCache = new ConcurrentReferenceHashMap<>();

  protected Class<?> requestClass;

  public AnnotatedRequest() {
    requestClass = getClass();
    fieldMapperCache.computeIfAbsent(requestClass, k -> new HashMap<>());
    methodMapperCache.computeIfAbsent(requestClass, k -> new HashMap<>());
  }

  private static Map<Field, Member> buildFieldMapper(Class<?> requestClass, Class<?> targetClass) {
    Map<Field, Member> mapper = new HashMap<>();
    KReflectionUtils.doWithFields(requestClass, reqField -> {
      RequestProperty reqProp = reqField.getAnnotation(RequestProperty.class);
      String propName = reqField.getName();
      if (reqProp != null && !reqProp.targetProperty().equals("")) {
        propName = reqProp.targetProperty();
      }
      Field targetField = KReflectionUtils.findField(targetClass, propName);
      Method targetSetter = KReflectionUtils.findActualMethod(targetClass, "set" + StringUtils.capitalize(propName), new Class[]{reqField.getType()});
      if (targetSetter != null) {
        mapper.put(reqField, targetSetter);
      } else {
        if (targetField != null) {
          mapper.put(reqField, targetField);
        }
      }
    });
    return mapper;
  }

  private static Map<Method, Member> buildMethodMapper(Class<?> requestClass, Class<?> targetClass) {
    Map<Method, Member> mapper = new HashMap<>();
    KReflectionUtils.doWithMethods(requestClass, reqMethod -> {
      RequestProperty reqProp = reqMethod.getAnnotation(RequestProperty.class);
      String propName = reqMethod.getName();
      if (reqProp != null && !reqProp.targetProperty().equals("")) {
        propName = reqProp.targetProperty();
      }
      Field targetField = KReflectionUtils.findField(targetClass, propName);
      Method targetSetter = KReflectionUtils.findActualMethod(targetClass, "set" + StringUtils.capitalize(propName), new Class[]{reqMethod.getReturnType()});
      if (targetSetter != null) {
        mapper.put(reqMethod, targetSetter);
      } else {
        if (targetField != null) {
          mapper.put(reqMethod, targetField);
        }
      }
    });
    return mapper;
  }


  public <T> T to(T targetObject, Class<T> targetClazz) {
    Map<Class<?>, Map<Field, Member>> fieldMapper = fieldMapperCache.get(requestClass);
    Map<Field, Member> requestFieldMapper = fieldMapper.get(targetClazz);
    if (requestFieldMapper == null) {
      requestFieldMapper = buildFieldMapper(requestClass, targetClazz);
      fieldMapper.put(requestClass, requestFieldMapper);
    }

    for (Field srcField : requestFieldMapper.keySet()) {
      srcField.setAccessible(true);
      Member member = requestFieldMapper.get(srcField);
      if (member.getClass().equals(Field.class)) {
        Field targetField = (Field) member;
        targetField.setAccessible(true);
        try {
          targetField.set(targetObject, srcField.get(this));
        } catch (IllegalAccessException e) {
          logger.debug(String.format("The <%s> of <%s> can't be valued by the <%s>!", targetField.getName(), targetClazz.getClass().getCanonicalName(), srcField.getName()));
        }
      } else if (member.getClass().equals(Method.class)) {
        Method targetSetter = (Method) member;
        try {
          targetSetter.invoke(targetObject, srcField.get(this));
        } catch (IllegalAccessException | InvocationTargetException e) {
          logger.debug(String.format("The <%s> of <%s> can't be valued by the <%s>!", targetSetter.getName(), targetClazz.getClass().getCanonicalName(), srcField.getName()));
        }
      }
    }

    Map<Class<?>, Map<Method, Member>> methodMapper = methodMapperCache.get(requestClass);
    Map<Method, Member> requestMethodMapper = methodMapper.get(targetClazz);
    if (requestMethodMapper == null) {
      requestMethodMapper = buildMethodMapper(requestClass, targetClazz);
      methodMapper.put(targetClazz, requestMethodMapper);
    }
    for (Method srcMethod : requestMethodMapper.keySet()) {
      srcMethod.setAccessible(true);
      Member member = requestMethodMapper.get(srcMethod);
      if (member.getClass().equals(Field.class)) {
        Field targetField = (Field) member;
        targetField.setAccessible(true);
        try {
          targetField.set(targetObject, srcMethod.invoke(this));
        } catch (IllegalAccessException | InvocationTargetException e) {
          logger.debug(String.format("The <%s> of <%s> can't be valued by the <%s>!", targetField.getName(), targetClazz.getClass().getCanonicalName(), srcMethod.getName()));
        }
      } else if (member.getClass().equals(Method.class)) {
        Method targetSetter = (Method) member;
        try {
          targetSetter.invoke(targetObject, srcMethod.invoke(this));
        } catch (IllegalAccessException | InvocationTargetException e) {
          logger.debug(String.format("The <%s> of <%s> can't be valued by the <%s>!", targetSetter.getName(), targetClazz.getClass().getCanonicalName(), srcMethod.getName()));
        }
      }
    }

    return targetObject;
  }

  public <T> T to(Class<T> targetClazz) {
    try {
      return to(targetClazz.newInstance(), targetClazz);
    } catch (InstantiationException | IllegalAccessException e) {
      logger.debug(String.format("The <%s> can't be initialized by newInstance! Please use method to(targetObject, targetClazz) with the initialized object instead.",
                                 targetClazz.getClass().getCanonicalName()));
      return null;
    }
  }

}

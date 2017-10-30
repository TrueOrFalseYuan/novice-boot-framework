package cn.kinkii.noviceboot.framework.controller.response;


import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.springframework.util.ConcurrentReferenceHashMap;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseResponse {

  private static final Map<Class<?>, List<Class<?>>> sourceClassesCache = new ConcurrentReferenceHashMap<>();
  private static final Map<Class<?>, Map<Class<?>, Map<Field, Method>>> fieldMapperCache = new ConcurrentReferenceHashMap<>();
  private static final Map<Class<?>, Map<Class<?>, Map<Method, Method>>> methodMapperCache = new ConcurrentReferenceHashMap<>();

  protected Class<?> responseClass;
  protected List<Class<?>> sourceClasses;
  protected Map<Class<?>, Map<Field, Method>> fieldMapper = new HashMap<>();
  protected Map<Class<?>, Map<Method, Method>> methodMapper = new HashMap<>();

  public BaseResponse() {
    responseClass = getClass();

    sourceClasses = sourceClassesCache.get(responseClass);
    if (sourceClasses == null) {
      sourceClasses = buildSourceClasses();
      sourceClassesCache.put(responseClass, sourceClasses);
    }

    fieldMapper = fieldMapperCache.get(responseClass);
    if (fieldMapper == null) {
      fieldMapper = buildFieldMapper();
      fieldMapperCache.put(responseClass, fieldMapper);
    }

    methodMapper = methodMapperCache.get(responseClass);
    if (methodMapper == null) {
      methodMapper = buildMethodMapper();
      methodMapperCache.put(responseClass, methodMapper);
    }
  }

  protected abstract List<Class<?>> buildSourceClasses();

  protected abstract Map<Class<?>, Map<Field, Method>> buildFieldMapper();

  protected abstract Map<Class<?>, Map<Method, Method>> buildMethodMapper();

  public void from(Object... sources) {
    for (Object src : sources) {
      if (!sourceClasses.contains(src.getClass())) {
        String validateClass = String.join(",", Lists.transform(sourceClasses, new Function<Class<?>, String>() {
          @Nullable
          @Override
          public String apply(@Nullable Class<?> input) {
            return input != null ? input.getCanonicalName() : null;
          }
        }));
        throw new IllegalArgumentException(String.format("Illegal source object class! The <%s> could build from %s.",
                                                         responseClass.getCanonicalName(), validateClass
        ));
      }

      Map<Field, Method> clzFieldMapper = fieldMapper.get(src.getClass());
      for (Field f : clzFieldMapper.keySet()) {
        f.setAccessible(true);
        Method getter = clzFieldMapper.get(f);
        try {
          f.set(this, getter.invoke(src));
        } catch (IllegalAccessException | InvocationTargetException e) {
          throw new IllegalStateException(
              String.format("The <%s> can't be valued by the method <%s> of <%s>!", f.getName(), getter.getName(),
                            src.getClass().getCanonicalName()
              ));
        }
      }

      Map<Method, Method> clzMethodMapper = methodMapper.get(src.getClass());
      for (Method m : clzMethodMapper.keySet()) {
        m.setAccessible(true);
        Method getter = clzMethodMapper.get(m);

        try {
          m.invoke(this, getter.invoke(src));
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
          throw new IllegalStateException(
              String.format("The method <%s> can't be valued by the method <%s> of <%s>!", m.getName(),
                            getter.getName(), src.getClass().getCanonicalName()
              ));
        }
      }
    }
  }

}

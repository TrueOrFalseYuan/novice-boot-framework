package cn.kinkii.novice.framework.utils;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class KReflectionUtils extends ReflectionUtils {

    public static Method findActualMethod(Class<?> clazz, String name) {
        return findActualMethod(clazz, name, null, null);
    }

    public static Method findActualMethod(Class<?> clazz, String name, Class<?>[] pTypes) {
        return findActualMethod(clazz, name, pTypes, null);
    }

    public static Method findActualMethod(Class<?> clazz, String name, Class<?> returnType) {
        return findActualMethod(clazz, name, null, returnType);
    }

    public static Method findActualMethod(Class<?> clazz, String name, Class<?>[] pTypes, Class<?> returnType) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(name, "Method name must not be null");

        List<Method> results = new ArrayList<>();
        doWithMethods(clazz, results::add, m -> (name.equals(m.getName()) &&
                (pTypes == null || Arrays.equals(pTypes, m.getParameterTypes())) &&
                (returnType == null || returnType == m.getReturnType())));

        if (results.size() == 0) {
            return null;
        } else if (results.size() == 1) {
            return results.get(0);
        } else {
            throw new IllegalStateException(
                    String.format("Class<%s> has <%d> methods named <%s>, please specify the parameter types or the return type!",
                            clazz.getCanonicalName(), results.size(), name
                    ));
        }
    }

}

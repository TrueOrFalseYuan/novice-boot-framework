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
                (pTypes == null || compareTypeLists(pTypes, m.getParameterTypes())) &&
                (returnType == null || returnType == m.getReturnType())));

        if (results.size() == 0) {
            return null;
        } else {
            return results.get(0);
        }
    }

    private static boolean compareTypeLists(Class<?>[] t1, Class<?>[] t2) {
        if (Arrays.equals(t1, t2)) {
            return true;
        } else {
            if (t1.length == t2.length) {
                return IntStream.range(0, t1.length).allMatch(i -> t2[i].isAssignableFrom(t1[i]));
            } else {
                return false;
            }
        }
    }

}

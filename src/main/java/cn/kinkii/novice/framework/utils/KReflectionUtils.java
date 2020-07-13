package cn.kinkii.novice.framework.utils;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KReflectionUtils extends ReflectionUtils {


    public static Method findActualMethod(Class<?> clazz, String name) {
        return findActualMethod(clazz, name, new Class[]{}, null);
    }

    public static Method findActualMethod(Class<?> clazz, String name, Class<?>[] pTypes) {
        return findActualMethod(clazz, name, pTypes, null);
    }

    public static Method findActualMethod(Class<?> clazz, String name, Class<?> returnType) {
        return findActualMethod(clazz, name, new Class[]{}, returnType);
    }

    public static Method findActualMethod(Class<?> clazz, String name, Class<?>[] pTypes, Class<?> returnType) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(name, "Method name must not be null");

        List<Method> results = new ArrayList<>();
        doWithMethods(clazz, results::add, m -> (name.equals(m.getName()) &&
                (pTypes == null || Arrays.equals(pTypes, m.getParameterTypes())) &&
                (returnType == null || returnType == m.getReturnType() )));

        if (results.size() == 0) {
            return null;
        } else {
            // 可改进
            return results.get(0);
        }

    }

    public static List<Field> getFields(Class clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
}

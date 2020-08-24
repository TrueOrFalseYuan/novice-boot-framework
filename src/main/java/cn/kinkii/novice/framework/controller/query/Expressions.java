package cn.kinkii.novice.framework.controller.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class Expressions {

    @SuppressWarnings("unchecked")
    public static List<Object> handleIterableValue(Object value) {
        List<Object> results = new ArrayList<>();
        if (value.getClass().isArray()) {
            results.addAll(Arrays.asList((Object[]) (value)));
        } else if (value instanceof Collection) {
            results.addAll((Collection<Object>) value);
        } else {
            results.add(value);
        }
        return results;
    }

    public static boolean isIterableValue(Object value) {
        return value.getClass().isArray() || value instanceof Collection;
    }

}

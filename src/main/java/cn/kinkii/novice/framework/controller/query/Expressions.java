package cn.kinkii.novice.framework.controller.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class Expressions {

    public static  <T> List<T> handleIterableValue(T value) {
        List<T> results = new ArrayList<>();
        if (value.getClass().isArray()) {
            results.addAll(Arrays.asList(((T[]) value)));
        } else if (Collection.class.isInstance(value)) {
            results.addAll((Collection<T>) value);
        } else {
            results.add(value);
        }
        return results;
    }

    public static boolean isIterableValue(Object value) {
        return value.getClass().isArray() || Collection.class.isInstance(value);
    }

    public static boolean isExpressionSupportIterableValue(Expression expression) {
        return expression.equals(Expression.IN) || expression.equals(Expression.NOT_IN);
    }
}

package cn.kinkii.novice.framework.controller.query;

import cn.kinkii.novice.framework.controller.query.annotations.OrderProperty;
import cn.kinkii.novice.framework.controller.query.annotations.QueryClass;
import cn.kinkii.novice.framework.utils.KReflectionUtils;

import org.springframework.data.util.ReflectionUtils;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BaseQuerySpecification<T extends BaseQuery> {

    protected T query;

    public BaseQuerySpecification(T query) {
        this.query = query;
    }

    private static Map<Class<?>, List<OrderProperty>> orderCache = new ConcurrentReferenceHashMap<>();

    protected Junction getClassJunction() {
        Junction result;
        if (query.getClass().isAnnotationPresent(QueryClass.class)) {
            result = query.getClass().getAnnotation(QueryClass.class).junction();
        } else {
            result = Junction.AND;
        }
        return result;
    }

    protected List<Order> getClassOrders() {
        if (orderCache.get(query.getClass()) == null) {
            List<OrderProperty> orderProperties = new ArrayList<>();
            if (query.getClass().isAnnotationPresent(QueryClass.class)) {
                OrderProperty[] orders = query.getClass().getAnnotation(QueryClass.class).orders();
                if (orders.length > 0) {
                    orderProperties.addAll(Arrays.asList(orders));
                }
            }
            KReflectionUtils.doWithFields(query.getClass(), field -> orderProperties.add(field.getAnnotation(OrderProperty.class)), new ReflectionUtils.AnnotationFieldFilter(OrderProperty.class));
            orderCache.put(query.getClass(), orderProperties);
        }
        return orderCache.get(query.getClass()).stream().map(p -> new cn.kinkii.novice.framework.controller.query.Order(p.column(), p.direction())).collect(Collectors.toList());
    }
}

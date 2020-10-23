package cn.kinkii.novice.framework.controller.query.jpa;


import cn.kinkii.novice.framework.controller.query.Expression;
import cn.kinkii.novice.framework.controller.query.Join;
import cn.kinkii.novice.framework.controller.query.Order;
import cn.kinkii.novice.framework.controller.query.*;
import cn.kinkii.novice.framework.controller.query.annotations.QueryProperty;
import cn.kinkii.novice.framework.entity.Identifiable;
import cn.kinkii.novice.framework.utils.KReflectionUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.EntityType;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@SuppressWarnings("rawtypes")
public class JpaQuerySpecification<T extends Identifiable> extends BaseQuerySpecification<JpaQuery> implements Specification<T> {

    public JpaQuerySpecification(JpaQuery<T> query) {
        super(query);
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public Predicate toPredicate(Root<T> entityRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> allPredicates = new ArrayList<>();
        Map<String, List<Predicate>> groupPredicates = new HashMap<>();
        criteriaQuery.distinct(getClassDistinct());

        KReflectionUtils.doWithMethods(query.getClass(), method -> {
            QueryProperty queryProperty = method.getAnnotation(QueryProperty.class);

            KReflectionUtils.makeAccessible(method);
            Object value = null;
            try {
                value = method.invoke(query);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            if (value != null) {
                if (!(value instanceof String) || StringUtils.hasText((String) value)) {
                    if (StringUtils.hasText(queryProperty.group())) {
                        groupPredicates.computeIfAbsent(queryProperty.group(), k -> new ArrayList<>())
                                .add(buildPredicate(criteriaBuilder, entityRoot, queryProperty, value));
                    } else {
                        allPredicates.add(buildPredicate(criteriaBuilder, entityRoot, queryProperty, value));
                    }
                }
            }
//            field.setAccessible(false);
        }, method -> method.getName().startsWith("get") && method.isAnnotationPresent(QueryProperty.class));

        KReflectionUtils.doWithFields(query.getClass(), field -> {
            QueryProperty queryProperty = field.getAnnotation(QueryProperty.class);

            KReflectionUtils.makeAccessible(field);
            Object value = field.get(query);
            if (value != null) {
                if (!(value instanceof String) || StringUtils.hasText((String) value)) {
                    if (StringUtils.hasText(queryProperty.group())) {
                        groupPredicates.computeIfAbsent(queryProperty.group(), k -> new ArrayList<>())
                                .add(buildPredicate(criteriaBuilder, entityRoot, queryProperty, value));
                    } else {
                        allPredicates.add(buildPredicate(criteriaBuilder, entityRoot, queryProperty, value));
                    }
                }
            }
//            field.setAccessible(false);
        }, field -> field.isAnnotationPresent(QueryProperty.class));

        Predicate result = null;
        Junction classJunction = getClassJunction();
        if (Junction.AND.equals(classJunction)) {
            groupPredicates.keySet().forEach(s -> allPredicates.add(criteriaBuilder.or(groupPredicates.get(s).toArray(new Predicate[0]))));
            result = criteriaBuilder.and(allPredicates.toArray(new Predicate[0]));
        } else if (Junction.OR.equals(classJunction)) {
            groupPredicates.keySet().forEach(s -> allPredicates.add(criteriaBuilder.and(groupPredicates.get(s).toArray(new Predicate[0]))));
            if (allPredicates.size() > 0) {
                result = criteriaBuilder.or(allPredicates.toArray(new Predicate[0]));
            }
        }

        if (query.getIsSortByAnnotation()) {
            List<Order> queryOrders = getClassOrders();
            if (queryOrders.size() != 0) {
                List<javax.persistence.criteria.Order> orders = new ArrayList<>();
                queryOrders.forEach(order -> {
                    if (order.getDirection().isDescending()) {
                        orders.add(criteriaBuilder.desc(getPath(order.getColumn(), entityRoot)));
                    } else {
                        orders.add(criteriaBuilder.asc(getPath(order.getColumn(), entityRoot)));
                    }
                });
                criteriaQuery.orderBy(orders);
            }
        }

        return result;
    }

    private Predicate buildPredicate(CriteriaBuilder criteriaBuilder, Root<T> entityRoot, QueryProperty queryProperty, Object value) {
        return JpaExpressions.by(queryProperty.expression())
                .build(
                        criteriaBuilder,
                        getPath(queryProperty.column(), entityRoot, getJoinType(queryProperty.join())),
                        getValue(queryProperty, value)
                );
    }

    private Object getValue(QueryProperty queryProperty, Object value) {
        if (Expression.NOT_LIKE.equals(queryProperty.expression()) || Expression.LIKE.equals(queryProperty.expression()) || Expression.LIKE_AND.equals(queryProperty.expression())) {
            if (value instanceof String) {
                return JpaMatches.by(queryProperty.match()).toMatchString((String) value);
            } else if (value.getClass().isArray() || value instanceof Collection) {
                List<String> results = new ArrayList<>();
                if (value.getClass().isArray()) {
                    //noinspection ConstantConditions
                    Arrays.asList((Object[]) (value)).forEach(o -> {
                        if (o instanceof String) {
                            results.add(JpaMatches.by(queryProperty.match()).toMatchString((String) o));
                        } else {
                            throw new IllegalStateException("Please use List<String> type for expression of LIKE, LIKE_AND or NOT_LIKE!");
                        }
                    });
                } else if (value instanceof Collection) {
                    //noinspection unchecked
                    ((Collection<Object>) value).forEach(o -> {
                        if (o instanceof String) {
                            results.add(JpaMatches.by(queryProperty.match()).toMatchString((String) o));
                        } else {
                            throw new IllegalStateException("Please use List<String> type for expression of LIKE, LIKE_AND or NOT_LIKE!");
                        }
                    });
                }
                return results;
            } else {
                throw new IllegalStateException("Please use String type for expression of LIKE or NOT_LIKE!");
            }
        }
        return value;
    }

    private JoinType getJoinType(Join join) {
        if (Join.DEFAULT.equals(join)) {
            return null;
        } else if (Join.JPA_INNER.equals(join)) {
            return JoinType.INNER;
        } else if (Join.JPA_LEFT.equals(join)) {
            return JoinType.LEFT;
        } else if (Join.JPA_RIGHT.equals(join)) {
            return JoinType.RIGHT;
        }
        throw new IllegalStateException("Unsupported join type in jpa query! - " + join.name());
    }

    @SuppressWarnings("rawtypes")
    private Path getPath(String columnName, Root<T> entityRoot) {
        return getPath(columnName, entityRoot, null);
    }

    @SuppressWarnings("rawtypes")
    private Path getPath(String columnName, Root<T> entityRoot, JoinType joinType) {
        EntityType<T> entityType = entityRoot.getModel();
        Path path;
        if (columnName.indexOf(".") > 0) { // 带Join查询
            String[] columns = columnName.split("\\.");
            From from = entityRoot;
            for (int i = 0; i < columns.length - 1; i++) {// 不包含最后一位
                boolean existence = false;
                for (Object o : from.getJoins()) {
                    javax.persistence.criteria.Join join = (javax.persistence.criteria.Join) o;
                    if (join.getAttribute().getName().equals(columns[i])) {
                        existence = true;
                        from = join;
                        break;
                    }
                }
                if (!existence) {
                    if (joinType == null) {
                        from = from.join(columns[i]);
                    } else {
                        from = from.join(columns[i], joinType);
                    }
                }
            }
            path = from.get(columns[columns.length - 1]);
        } else {
            path = entityRoot.get(entityType.getSingularAttribute(columnName));
        }
        return path;
    }
}

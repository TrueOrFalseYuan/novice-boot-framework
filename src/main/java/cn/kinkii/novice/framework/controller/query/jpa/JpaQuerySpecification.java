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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JpaQuerySpecification<T extends Identifiable> extends BaseQuerySpecification<JpaQuery> implements Specification<T> {

    public JpaQuerySpecification(JpaQuery<T> query) {
        super(query);
    }

    @Override
    public Predicate toPredicate(Root<T> entityRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> allPredicates = new ArrayList<>();
        Map<String, List<Predicate>> groupPredicates = new HashMap<>();

        KReflectionUtils.doWithFields(query.getClass(), field -> {
            QueryProperty queryProperty = field.getAnnotation(QueryProperty.class);

            KReflectionUtils.makeAccessible(field);
            Object value = field.get(query);
            if (value != null) {
                if (StringUtils.hasText(queryProperty.group())) {
                    List<Predicate> predicates = groupPredicates.computeIfAbsent(queryProperty.group(), k -> new ArrayList<>());
                    predicates.add(buildPredicate(criteriaBuilder, entityRoot, queryProperty, value));
                } else {
                    allPredicates.add(buildPredicate(criteriaBuilder, entityRoot, queryProperty, value));
                }

            }
            field.setAccessible(false);
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
                queryOrders.forEach(order -> {
                    if (order.getDirection().isDescending()) {
                        criteriaQuery.orderBy(criteriaBuilder.desc(getPath(order.getColumn(), entityRoot)));
                    } else {
                        criteriaQuery.orderBy(criteriaBuilder.asc(getPath(order.getColumn(), entityRoot)));
                    }
                });
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
        if (Expression.NOT_LIKE.equals(queryProperty.expression()) || Expression.LIKE.equals(queryProperty.expression())) {
            if (value instanceof String) {
                return JpaMatches.by(queryProperty.match()).toMatchString((String) value);
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

    private Path getPath(String columnName, Root<T> entityRoot) {
        return getPath(columnName, entityRoot, null);
    }

    private Path getPath(String columnName, Root<T> entityRoot, JoinType joinType) {
        EntityType<T> entityType = entityRoot.getModel();
        Path path;
        if (columnName.indexOf(".") > 0) { // 带Join查询
            String[] columns = columnName.split("\\.");
            From from = entityRoot;
            boolean existence = false;
            for (int i = 0; i < columns.length - 1; i++) {// 不包含最后一位
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

package cn.kinkii.novice.framework.controller.query.jpa;

import cn.kinkii.novice.framework.controller.query.Expression;
import cn.kinkii.novice.framework.controller.query.Expressions;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "WeakerAccess"})
public class JpaExpressions extends Expressions{

    private static Map<Expression, JpaExpression> expressionsMap = new HashMap<>();

    static {
        expressionsMap.put(Expression.EQ, (builder, path, value) -> {
            if(isIterableValue(value)) {
                List<Predicate> predicateList = new ArrayList<>();
                handleIterableValue(value).forEach(e -> {
                    predicateList.add(builder.equal(path, (Comparable) e));
                });
                return builder.or(predicateList.toArray(new Predicate[]{}));
            }
            return builder.equal(path, (Comparable) value);
        });
        expressionsMap.put(Expression.NEQ, (builder, path, value) -> {
            if(isIterableValue(value)) {
                List<Predicate> predicateList = new ArrayList<>();
                handleIterableValue(value).forEach(e -> {
                    predicateList.add(builder.notEqual(path, (Comparable) e));
                });
                return builder.or(predicateList.toArray(new Predicate[]{}));
            }
            return builder.notEqual(path, (Comparable) value);
        });

        expressionsMap.put(Expression.GT, (builder, path, value) -> {
            if(isIterableValue(value)) {
                List<Predicate> predicateList = new ArrayList<>();
                handleIterableValue(value).forEach(e -> {
                    predicateList.add(builder.greaterThan(path, (Comparable) e));
                });
                return builder.or(predicateList.toArray(new Predicate[]{}));
            }
            return builder.greaterThan(path, (Comparable) value);
        });
        expressionsMap.put(Expression.LT, (builder, path, value) -> {
            if(isIterableValue(value)) {
                List<Predicate> predicateList = new ArrayList<>();
                handleIterableValue(value).forEach(e -> {
                    predicateList.add(builder.lessThan(path, (Comparable) e));
                });
                return builder.or(predicateList.toArray(new Predicate[]{}));
            }
            return builder.lessThan(path, (Comparable) value);
        });
        expressionsMap.put(Expression.GTE, (builder, path, value) -> {
            if(isIterableValue(value)) {
                List<Predicate> predicateList = new ArrayList<>();
                handleIterableValue(value).forEach(e -> {
                    predicateList.add(builder.greaterThanOrEqualTo(path, (Comparable) e));
                });
                return builder.or(predicateList.toArray(new Predicate[]{}));
            }
            return builder.greaterThanOrEqualTo(path, (Comparable) value);
        });
        expressionsMap.put(Expression.LTE, (builder, path, value) -> {
            if(isIterableValue(value)) {
                List<Predicate> predicateList = new ArrayList<>();
                handleIterableValue(value).forEach(e -> {
                    predicateList.add(builder.lessThanOrEqualTo(path, (Comparable) e));
                });
                return builder.or(predicateList.toArray(new Predicate[]{}));
            }
            return builder.lessThanOrEqualTo(path, (Comparable) value);
        });

        expressionsMap.put(Expression.LIKE, (builder, path, value) -> {
            if(isIterableValue(value)) {
                List<Predicate> predicateList = new ArrayList<>();
                handleIterableValue(value).forEach(e -> {
                    predicateList.add(builder.like(path, (String) e));
                });
                return builder.or(predicateList.toArray(new Predicate[]{}));
            }
            return builder.like(path, (String) value);
        });
        expressionsMap.put(Expression.NOT_LIKE, (builder, path, value) -> {
            if(isIterableValue(value)) {
                List<Predicate> predicateList = new ArrayList<>();
                handleIterableValue(value).forEach(e -> {
                    predicateList.add(builder.notLike(path, (String) e));
                });
                return builder.or(predicateList.toArray(new Predicate[]{}));
            }
            return builder.notLike(path, (String) value);
        });
        expressionsMap.put(Expression.IN, (builder, path, value) -> {
            CriteriaBuilder.In<Object> in = builder.in(path);
            handleIterableValue(value).forEach(in::value);
            return in;
        });
        expressionsMap.put(Expression.NOT_IN, (builder, path, value) -> {
            CriteriaBuilder.In<Object> in = builder.in(path);
            handleIterableValue(value).forEach(in::value);
            return builder.not(in);
        });
        expressionsMap.put(Expression.IS_NULL, (CriteriaBuilder builder, Path path, Object value) -> {
            if ((Boolean) value) {
                return builder.isNull(path);
            } else {
                return builder.isNotNull(path);
            }
        });
    }

    public static JpaExpression by(Expression exp) {
        return expressionsMap.get(exp);
    }
}

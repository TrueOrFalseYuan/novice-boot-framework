package cn.kinkii.novice.framework.controller.query.jpa;

import cn.kinkii.novice.framework.controller.query.Expression;
import cn.kinkii.novice.framework.controller.query.Expressions;
import cn.kinkii.novice.framework.db.mysql.KMySQLFunction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.*;

@SuppressWarnings({"unchecked", "WeakerAccess", "rawtypes"})
public class JpaExpressions extends Expressions {

    private static final Map<Expression, JpaExpression> expressionsMap = new HashMap<>();

    static {
        expressionsMap.put(Expression.EQ, (builder, path, value) -> {
            if (isIterableValue(value)) {
                List<Predicate> predicateList = new ArrayList<>();
                handleIterableValue(value).forEach(e -> {
                    predicateList.add(builder.equal(path, e));
                });
                return builder.or(predicateList.toArray(new Predicate[]{}));
            }
            return builder.equal(path, value);
        });
        expressionsMap.put(Expression.NEQ, (builder, path, value) -> {
            if (isIterableValue(value)) {
                List<Predicate> predicateList = new ArrayList<>();
                handleIterableValue(value).forEach(e -> {
                    predicateList.add(builder.notEqual(path, e));
                });
                return builder.or(predicateList.toArray(new Predicate[]{}));
            }
            return builder.notEqual(path, value);
        });

        expressionsMap.put(Expression.GT, (builder, path, value) -> {
            if (isIterableValue(value)) {
                List<Predicate> predicateList = new ArrayList<>();
                handleIterableValue(value).forEach(e -> {
                    predicateList.add(builder.greaterThan(path, (Comparable) e));
                });
                return builder.or(predicateList.toArray(new Predicate[]{}));
            }
            return builder.greaterThan(path, (Comparable) value);
        });
        expressionsMap.put(Expression.LT, (builder, path, value) -> {
            if (isIterableValue(value)) {
                List<Predicate> predicateList = new ArrayList<>();
                handleIterableValue(value).forEach(e -> {
                    predicateList.add(builder.lessThan(path, (Comparable) e));
                });
                return builder.or(predicateList.toArray(new Predicate[]{}));
            }
            return builder.lessThan(path, (Comparable) value);
        });
        expressionsMap.put(Expression.GTE, (builder, path, value) -> {
            if (isIterableValue(value)) {
                List<Predicate> predicateList = new ArrayList<>();
                handleIterableValue(value).forEach(e -> {
                    predicateList.add(builder.greaterThanOrEqualTo(path, (Comparable) e));
                });
                return builder.or(predicateList.toArray(new Predicate[]{}));
            }
            return builder.greaterThanOrEqualTo(path, (Comparable) value);
        });
        expressionsMap.put(Expression.LTE, (builder, path, value) -> {
            if (isIterableValue(value)) {
                List<Predicate> predicateList = new ArrayList<>();
                handleIterableValue(value).forEach(e -> {
                    predicateList.add(builder.lessThanOrEqualTo(path, (Comparable) e));
                });
                return builder.or(predicateList.toArray(new Predicate[]{}));
            }
            return builder.lessThanOrEqualTo(path, (Comparable) value);
        });

        expressionsMap.put(Expression.LIKE, (builder, path, value) -> {
            if (isIterableValue(value)) {
                List<Predicate> predicateList = new ArrayList<>();
                handleIterableValue(value).forEach(e -> {
                    predicateList.add(builder.like(path, (String) e));
                });
                return builder.or(predicateList.toArray(new Predicate[]{}));
            }
            return builder.like(path, (String) value);
        });
        expressionsMap.put(Expression.NOT_LIKE, (builder, path, value) -> {
            if (isIterableValue(value)) {
                List<Predicate> predicateList = new ArrayList<>();
                handleIterableValue(value).forEach(e -> {
                    predicateList.add(builder.notLike(path, (String) e));
                });
                return builder.or(predicateList.toArray(new Predicate[]{}));
            }
            return builder.notLike(path, (String) value);
        });
        // Match expression is Only supported while using KMySQLDialect
        expressionsMap.put(Expression.MATCH, (builder, path, value) -> {
            if (isIterableValue(value)) {
                StringJoiner againstValueJoiner = new StringJoiner(" ");
                handleIterableValue(value).forEach(e -> {
                    againstValueJoiner.add("+\"" + value + "\"");
                });
                return builder.greaterThan(builder.function(KMySQLFunction.MATCH.name(), Double.class, path, builder.literal((String) againstValueJoiner.toString())), 0.0);
            } else {
                if (value instanceof String) {
                    return builder.greaterThan(builder.function(KMySQLFunction.MATCH.name(), Double.class, path, builder.literal("+\"" + value + "\"")), 0.0);
                } else {
                    throw new IllegalArgumentException("The value should be a string while using MATCH expression!");
                }
            }
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

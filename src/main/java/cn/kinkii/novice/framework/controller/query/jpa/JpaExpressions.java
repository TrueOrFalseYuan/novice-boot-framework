package cn.kinkii.novice.framework.controller.query.jpa;

import cn.kinkii.novice.framework.controller.query.Expression;
import cn.kinkii.novice.framework.controller.query.Expressions;
import cn.kinkii.novice.framework.controller.query.annotations.QueryProperty;
import cn.kinkii.novice.framework.db.mysql.KMySQLFunction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.*;

@SuppressWarnings({"unchecked", "WeakerAccess", "rawtypes"})
public class JpaExpressions extends Expressions {

    private static final Map<Expression, JpaExpression> expressionsMap = new HashMap<>();

    static {
        expressionsMap.put(Expression.EQ, (builder, path, value, property) -> {
            if (isIterableValue(value)) {
                List<Predicate> predicateList = new ArrayList<>();
                List<Object> iterable = handleIterableValue(value);
                if (property.ignoreCase() && iterable.size() > 0 && iterable.get(0) instanceof String) {
                    iterable.forEach(e -> {
                        predicateList.add(builder.equal(builder.upper(path), ((String) e).toUpperCase()));
                    });
                } else {
                    handleIterableValue(value).forEach(e -> {
                        predicateList.add(builder.equal(path, e));
                    });
                }
                return builder.or(predicateList.toArray(new Predicate[]{}));
            }
            if (value instanceof String) {
                return builder.equal(builder.upper(path), ((String) value).toUpperCase());
            }
            return builder.equal(path, value);
        });
        expressionsMap.put(Expression.ALL_EQ, (builder, path, value, property) -> {
            if (isIterableValue(value)) {
                List<Predicate> predicateList = new ArrayList<>();
                List<Object> iterable = handleIterableValue(value);
                if (property.ignoreCase() && iterable.size() > 0 && iterable.get(0) instanceof String) {
                    iterable.forEach(e -> {
                        predicateList.add(builder.equal(builder.upper(path), ((String) e).toUpperCase()));
                    });
                } else {
                    handleIterableValue(value).forEach(e -> {
                        predicateList.add(builder.equal(path, e));
                    });
                }
                return builder.and(predicateList.toArray(new Predicate[]{}));
            }
            if (value instanceof String) {
                return builder.equal(builder.upper(path), ((String) value).toUpperCase());
            }
            return builder.equal(path, value);
        });
        expressionsMap.put(Expression.NEQ, (builder, path, value, property) -> {
            if (isIterableValue(value)) {
                List<Predicate> predicateList = new ArrayList<>();
                List<Object> iterable = handleIterableValue(value);
                if (property.ignoreCase() && iterable.size() > 0 && iterable.get(0) instanceof String) {
                    iterable.forEach(e -> {
                        predicateList.add(builder.notEqual(builder.upper(path), ((String) e).toUpperCase()));
                    });
                } else {
                    handleIterableValue(value).forEach(e -> {
                        predicateList.add(builder.notEqual(path, e));
                    });
                }
                return builder.or(predicateList.toArray(new Predicate[]{}));
            }
            if (value instanceof String) {
                return builder.notEqual(builder.upper(path), ((String) value).toUpperCase());
            }
            return builder.notEqual(path, value);
        });
        expressionsMap.put(Expression.ALL_NEQ, (builder, path, value, property) -> {
            if (isIterableValue(value)) {
                List<Predicate> predicateList = new ArrayList<>();
                List<Object> iterable = handleIterableValue(value);
                if (property.ignoreCase() && iterable.size() > 0 && iterable.get(0) instanceof String) {
                    iterable.forEach(e -> {
                        predicateList.add(builder.notEqual(builder.upper(path), ((String) e).toUpperCase()));
                    });
                } else {
                    handleIterableValue(value).forEach(e -> {
                        predicateList.add(builder.notEqual(path, e));
                    });
                }
                return builder.and(predicateList.toArray(new Predicate[]{}));
            }
            if (value instanceof String) {
                return builder.notEqual(builder.upper(path), ((String) value).toUpperCase());
            }
            return builder.notEqual(path, value);
        });

        expressionsMap.put(Expression.GT, (builder, path, value, property) -> {
            if (isIterableValue(value)) {
                List<Predicate> predicateList = new ArrayList<>();
                handleIterableValue(value).forEach(e -> {
                    predicateList.add(builder.greaterThan(path, (Comparable) e));
                });
                return builder.or(predicateList.toArray(new Predicate[]{}));
            }
            return builder.greaterThan(path, (Comparable) value);
        });
        expressionsMap.put(Expression.LT, (builder, path, value, property) -> {
            if (isIterableValue(value)) {
                List<Predicate> predicateList = new ArrayList<>();
                handleIterableValue(value).forEach(e -> {
                    predicateList.add(builder.lessThan(path, (Comparable) e));
                });
                return builder.or(predicateList.toArray(new Predicate[]{}));
            }
            return builder.lessThan(path, (Comparable) value);
        });
        expressionsMap.put(Expression.GTE, (builder, path, value, property) -> {
            if (isIterableValue(value)) {
                List<Predicate> predicateList = new ArrayList<>();
                handleIterableValue(value).forEach(e -> {
                    predicateList.add(builder.greaterThanOrEqualTo(path, (Comparable) e));
                });
                return builder.or(predicateList.toArray(new Predicate[]{}));
            }
            return builder.greaterThanOrEqualTo(path, (Comparable) value);
        });
        expressionsMap.put(Expression.LTE, (builder, path, value, property) -> {
            if (isIterableValue(value)) {
                List<Predicate> predicateList = new ArrayList<>();
                handleIterableValue(value).forEach(e -> {
                    predicateList.add(builder.lessThanOrEqualTo(path, (Comparable) e));
                });
                return builder.or(predicateList.toArray(new Predicate[]{}));
            }
            return builder.lessThanOrEqualTo(path, (Comparable) value);
        });
        expressionsMap.put(Expression.BETWEEN, (builder, path, value, property) -> {
            if (!isIterableValue(value)) {
                throw new IllegalArgumentException("The value should be iterable!");
            } else {
                List<Object> values = handleIterableValue(value);
                if (values.size() != 2) {
                    throw new IllegalArgumentException("The value should be contain 2 object!");
                } else {
                    if (values.get(0) instanceof Long) {
                        return builder.between((Path<Long>) path, (Long) values.get(0), (Long) values.get(1));
                    } else if (values.get(0) instanceof Integer) {
                        return builder.between((Path<Integer>) path, (Integer) values.get(0), (Integer) values.get(1));
                    } else if (values.get(0) instanceof Date) {
                        return builder.between((Path<Date>) path, (Date) values.get(0), (Date) values.get(1));
                    } else {
                        throw new IllegalArgumentException("Unsupported type for between expression! - " + values.get(0).getClass().getSimpleName());
                    }
                }
            }
        });
        expressionsMap.put(Expression.LIKE, (builder, path, value, property) -> {
            if (isIterableValue(value)) {
                List<Predicate> predicateList = new ArrayList<>();
                List<Object> iterable = handleIterableValue(value);
                if (property.ignoreCase() && iterable.size() > 0 && iterable.get(0) instanceof String) {
                    iterable.forEach(e -> {
                        predicateList.add(builder.like(builder.upper(path), ((String) e).toUpperCase()));
                    });
                } else {
                    handleIterableValue(value).forEach(e -> {
                        predicateList.add(builder.like(path, ((String) e)));
                    });
                }
                return builder.or(predicateList.toArray(new Predicate[]{}));
            }
            if (value instanceof String) {
                return builder.like(builder.upper(path), ((String) value).toUpperCase());
            }
            return builder.like(path, ((String) value));
        });
        expressionsMap.put(Expression.LIKE_AND, (builder, path, value, property) -> {
            if (isIterableValue(value)) {
                List<Predicate> predicateList = new ArrayList<>();
                List<Object> iterable = handleIterableValue(value);
                if (property.ignoreCase() && iterable.size() > 0 && iterable.get(0) instanceof String) {
                    iterable.forEach(e -> {
                        predicateList.add(builder.like(builder.upper(path), ((String) e).toUpperCase()));
                    });
                } else {
                    handleIterableValue(value).forEach(e -> {
                        predicateList.add(builder.like(path, ((String) e)));
                    });
                }
                return builder.and(predicateList.toArray(new Predicate[]{}));
            }
            if (value instanceof String) {
                return builder.like(builder.upper(path), ((String) value).toUpperCase());
            }
            return builder.like(path, ((String) value));
        });
        expressionsMap.put(Expression.NOT_LIKE, (builder, path, value, property) -> {
            if (isIterableValue(value)) {
                List<Predicate> predicateList = new ArrayList<>();
                List<Object> iterable = handleIterableValue(value);
                if (property.ignoreCase() && iterable.size() > 0 && iterable.get(0) instanceof String) {
                    iterable.forEach(e -> {
                        predicateList.add(builder.notLike(builder.upper(path), ((String) e).toUpperCase()));
                    });
                } else {
                    handleIterableValue(value).forEach(e -> {
                        predicateList.add(builder.notLike(path, ((String) e)));
                    });
                }
                return builder.or(predicateList.toArray(new Predicate[]{}));
            }
            if (value instanceof String) {
                return builder.notLike(builder.upper(path), ((String) value).toUpperCase());
            }
            return builder.notLike(path, ((String) value));
        });
        // Match expression is Only supported while using KMySQLDialect
        expressionsMap.put(Expression.MATCH, (builder, path, value, property) -> {
            if (isIterableValue(value)) {
                StringJoiner againstValueJoiner = new StringJoiner(" ");
                handleIterableValue(value).forEach(e -> {
                    againstValueJoiner.add("+\"" + value + "\"");
                });
                return builder.greaterThan(builder.function(KMySQLFunction.MATCH.name(), Double.class, path, builder.literal(againstValueJoiner.toString())), 0.0);
            } else {
                if (value instanceof String) {
                    return builder.greaterThan(builder.function(KMySQLFunction.MATCH.name(), Double.class, path, builder.literal("+\"" + value + "\"")), 0.0);
                } else {
                    throw new IllegalArgumentException("The value should be a string while using MATCH expression!");
                }
            }
        });

        expressionsMap.put(Expression.IN, (builder, path, value, property) -> {
            List<Object> iterable = handleIterableValue(value);
            if (property.ignoreCase() && iterable.size() > 0 && iterable.get(0) instanceof String) {
                CriteriaBuilder.In<Object> in = builder.in(builder.upper(path));
                iterable.forEach(str -> in.value(((String)str).toUpperCase()));
                return in;
            } else {
                CriteriaBuilder.In<Object> in = builder.in(path);
                iterable.forEach(in::value);
                return in;
            }
        });
        expressionsMap.put(Expression.NOT_IN, (builder, path, value, property) -> {
            List<Object> iterable = handleIterableValue(value);
            if (property.ignoreCase() && iterable.size() > 0 && iterable.get(0) instanceof String) {
                CriteriaBuilder.In<Object> in = builder.in(builder.upper(path));
                iterable.forEach(str -> in.value(((String)str).toUpperCase()));
                return builder.not(in);
            } else {
                CriteriaBuilder.In<Object> in = builder.in(path);
                iterable.forEach(in::value);
                return builder.not(in);
            }
        });
        expressionsMap.put(Expression.IS_NULL, (builder, path, value, property) -> {
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

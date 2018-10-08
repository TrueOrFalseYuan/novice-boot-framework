package cn.kinkii.novice.framework.controller.query.mongo;

import cn.kinkii.novice.framework.controller.query.Expression;
import cn.kinkii.novice.framework.controller.query.Expressions;

import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoExpressions extends Expressions {

    private static Map<Expression, MongoExpression> expressionsMap = new HashMap<>();

    static {
        expressionsMap.put(Expression.EQ, (queryProperty, value) -> {
            if(isIterableValue(value)) {
                List<Criteria> criteriaList = new ArrayList<>();
                handleIterableValue(value).forEach(e -> {
                    criteriaList.add(Criteria.where(queryProperty.column()).is(e));
                });
                return new Criteria().orOperator(criteriaList.toArray(new Criteria[]{}));
            }
            return Criteria.where(queryProperty.column()).is(value);
        });
        expressionsMap.put(Expression.NEQ, (queryProperty, value) -> {
            if(isIterableValue(value)) {
                List<Criteria> criteriaList = new ArrayList<>();
                handleIterableValue(value).forEach(e -> {
                    criteriaList.add(Criteria.where(queryProperty.column()).ne(e));
                });
                return new Criteria().orOperator(criteriaList.toArray(new Criteria[]{}));
            }
            return Criteria.where(queryProperty.column()).ne(value);
        });

        expressionsMap.put(Expression.GT, (queryProperty, value) -> {
            if(isIterableValue(value)) {
                List<Criteria> criteriaList = new ArrayList<>();
                handleIterableValue(value).forEach(e -> {
                    criteriaList.add(Criteria.where(queryProperty.column()).gt(e));
                });
                return new Criteria().orOperator(criteriaList.toArray(new Criteria[]{}));
            }
            return Criteria.where(queryProperty.column()).gt(value);
        });
        expressionsMap.put(Expression.LT, (queryProperty, value) -> {
            if(isIterableValue(value)) {
                List<Criteria> criteriaList = new ArrayList<>();
                handleIterableValue(value).forEach(e -> {
                    criteriaList.add(Criteria.where(queryProperty.column()).lt(e));
                });
                return new Criteria().orOperator(criteriaList.toArray(new Criteria[]{}));
            }
            return Criteria.where(queryProperty.column()).lt(value);
        });
        expressionsMap.put(Expression.GTE, (queryProperty, value) -> {
            if(isIterableValue(value)) {
                List<Criteria> criteriaList = new ArrayList<>();
                handleIterableValue(value).forEach(e -> {
                    criteriaList.add(Criteria.where(queryProperty.column()).gte(e));
                });
                return new Criteria().orOperator(criteriaList.toArray(new Criteria[]{}));
            }
            return Criteria.where(queryProperty.column()).gte(value);
        });
        expressionsMap.put(Expression.LTE, (queryProperty, value) -> {
            if(isIterableValue(value)) {
                List<Criteria> criteriaList = new ArrayList<>();
                handleIterableValue(value).forEach(e -> {
                    criteriaList.add(Criteria.where(queryProperty.column()).lte(e));
                });
                return new Criteria().orOperator(criteriaList.toArray(new Criteria[]{}));
            }
            return Criteria.where(queryProperty.column()).lte(value);
        });
        expressionsMap.put(Expression.REGEX, (queryProperty, value) -> {
            if(isIterableValue(value)) {
                List<Criteria> criteriaList = new ArrayList<>();
                handleIterableValue(value).forEach(e -> {
                    criteriaList.add(Criteria.where(queryProperty.column()).regex((String) e));
                });
                return new Criteria().orOperator(criteriaList.toArray(new Criteria[]{}));
            }
            return Criteria.where(queryProperty.column()).regex((String)value);
        });
        expressionsMap.put(Expression.IN, (queryProperty, value) -> Criteria.where(queryProperty.column()).in(handleIterableValue(value)));
        expressionsMap.put(Expression.NOT_IN, (queryProperty, value) -> Criteria.where(queryProperty.column()).nin(handleIterableValue(value)));
        expressionsMap.put(Expression.IS_NULL, (queryProperty, value) ->
                (boolean) value ? Criteria.where(queryProperty.column()).is(null) : Criteria.where(queryProperty.column()).ne(null)
        );
    }

    public static MongoExpression by(Expression exp) {
        return expressionsMap.get(exp);
    }
}

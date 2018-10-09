package cn.kinkii.novice.framework.controller.query.mongo;

import cn.kinkii.novice.framework.controller.query.BaseQuerySpecification;
import cn.kinkii.novice.framework.controller.query.Junction;
import cn.kinkii.novice.framework.controller.query.Order;
import cn.kinkii.novice.framework.controller.query.annotations.QueryProperty;
import cn.kinkii.novice.framework.entity.Identifiable;
import cn.kinkii.novice.framework.utils.KReflectionUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoSpecification<T extends Identifiable> extends BaseQuerySpecification<MongoQuery> {

    public MongoSpecification(MongoQuery<T> query) {
        super(query);
    }

    public Query toMongoDBQuery() {
        Query mongoDBQuery = new Query();
        Criteria criteria = new Criteria();
        if (query.getIsSortByAnnotation()) {
            List<Order> queryOrders = getClassOrders();
            if (!queryOrders.isEmpty()) {
                queryOrders.forEach(order -> {
                    mongoDBQuery.with(new Sort(order.getDirection().toDirection(), order.getColumn()));
                });
            }
        }

        List<Criteria> allCriteria = new ArrayList<>();
        List<Criteria> orList = new ArrayList<>();
        List<Criteria> andList = new ArrayList<>();
        Map<String, List<Criteria>> groupCriteriaMap = new HashMap<>();
        KReflectionUtils.doWithFields(query.getClass(), field -> {
            QueryProperty queryProperty = field.getAnnotation(QueryProperty.class);

            KReflectionUtils.makeAccessible(field);
            Object value = field.get(query);
            if (value != null) {
                if (StringUtils.hasText(queryProperty.group())) {
                    List<Criteria> criteriaList = groupCriteriaMap.computeIfAbsent(queryProperty.group(), k -> new ArrayList<>());
                    criteriaList.add(buildCriteria(queryProperty, value));
                } else {
                    allCriteria.add(buildCriteria(queryProperty, value));
                }

            }
            field.setAccessible(false);
        }, field -> field.isAnnotationPresent(QueryProperty.class));

        Junction classJunction = getClassJunction();

        if (Junction.AND.equals(classJunction)) {
            groupCriteriaMap.values().forEach(orList::addAll);
            andList.addAll(allCriteria);
        } else {
            groupCriteriaMap.values().forEach(andList::addAll);
            orList.addAll(allCriteria);
        }
        if (!andList.isEmpty()) {
            criteria.andOperator(andList.toArray(new Criteria[0]));
        }
        if (!orList.isEmpty()) {
            criteria.orOperator(orList.toArray(new Criteria[0]));
        }
        mongoDBQuery.addCriteria(criteria);
        return mongoDBQuery;
    }

    public Criteria buildCriteria(QueryProperty queryProperty, Object value) {
        return MongoExpressions.by(queryProperty.expression()).build(queryProperty, value);
    }
}

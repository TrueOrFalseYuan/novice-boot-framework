package cn.kinkii.novice.framework.controller.query.jpa;

import cn.kinkii.novice.framework.controller.query.annotations.QueryProperty;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

public interface JpaExpression {

    Predicate build(CriteriaBuilder builder, Path path, Object value, QueryProperty queryProperty);

}

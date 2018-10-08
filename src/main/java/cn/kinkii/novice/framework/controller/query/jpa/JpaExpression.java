package cn.kinkii.novice.framework.controller.query.jpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

public interface JpaExpression {

    Predicate build(CriteriaBuilder builder, Path path, Object value);
}

package cn.kinkii.novice.framework.controller.query.mongo;

import cn.kinkii.novice.framework.controller.query.annotations.QueryProperty;
import org.springframework.data.mongodb.core.query.Criteria;

public interface MongoExpression {

    Criteria build(QueryProperty queryProperty, Object value);

}

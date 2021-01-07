package cn.kinkii.novice.framework.controller.query;

import cn.kinkii.novice.framework.entity.Identifiable;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("rawtypes")
@Setter
@Getter
public abstract class BaseQuery<T extends Identifiable> {

    private Boolean isSortByAnnotation = true;

}

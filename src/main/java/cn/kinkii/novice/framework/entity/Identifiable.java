package cn.kinkii.novice.framework.entity;

import java.io.Serializable;

/**
 * 用于业务对象主键的公共接口.
 *
 * @param <ID> 主键类型
 * @author TrueOrFalse.Yuan
 */
public interface Identifiable<ID extends Serializable> extends Serializable {

    ID getId();

    void setId(ID id);

}

package cn.kinkii.noviceboot.framework.entity;

import java.io.Serializable;

/**
 * 用于业务对象主键的公共接口.
 * @author TrueOrFalse.Yuan
 * @param <ID> 主键类型
 */
public interface Identifiable<ID extends Serializable> {

	ID getId();

	void setId(ID id);
}

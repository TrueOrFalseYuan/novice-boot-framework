package cn.kinkii.noviceboot.framework.service;

import cn.kinkii.noviceboot.framework.entity.Identifiable;

import java.io.Serializable;

/**
 * 实体类Service，实现必要增/删/改/查方法
 *
 * @param <E>
 * @param <ID>
 */
public interface ModelService<E extends Identifiable<ID>, ID extends Serializable> {

    <T extends E> T create(T newModel);

    <T extends E> T update(T newModel);

    <T extends E> T patch(T newModelData);

    void delete(ID id);

    Boolean exists(ID id);

}

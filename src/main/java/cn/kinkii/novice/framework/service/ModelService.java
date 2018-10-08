package cn.kinkii.novice.framework.service;

import cn.kinkii.novice.framework.entity.Identifiable;
import cn.kinkii.novice.framework.entity.Identifiable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * 实体类Service，实现必要增/删/改/查方法
 *
 * @param <E>
 * @param <ID>
 */
public interface ModelService<E extends Identifiable<ID>, ID extends Serializable> {

    Optional<E> findById(ID id);

    List<E> findAll();

    Page<E> findAll(Pageable id);

    boolean existsById(ID id);

    void create(E model);

    void update(E model);

    void patch(E model);

    void delete(E model);

    void deleteById(ID id);

    void deleteInBatch(Iterable<E> entities);

    void deleteInBatchById(Iterable<ID> id);

}

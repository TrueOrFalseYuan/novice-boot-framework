package cn.kinkii.novice.framework.repository;

import cn.kinkii.novice.framework.entity.Identifiable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

import javax.transaction.Transactional;

@NoRepositoryBean
@Transactional
public interface ModelRepository<E extends Identifiable<ID>, ID extends Serializable> extends JpaRepository<E, ID>, JpaSpecificationExecutor<E>, QuerydslPredicateExecutor<E> {

    void create(E model);

    void update(E model);

    void patch(E model);

    void deleteById(ID id);

    void deleteInBatchById(Iterable<ID> id);
}

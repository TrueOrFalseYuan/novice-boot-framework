package cn.kinkii.novice.framework.repository;

import cn.kinkii.novice.framework.entity.Identifiable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import javax.transaction.Transactional;
import java.io.Serializable;

@NoRepositoryBean
@Transactional
public interface ModelRepository<E extends Identifiable<ID>, ID extends Serializable> extends JpaRepository<E, ID>, JpaSpecificationExecutor<E>, QuerydslPredicateExecutor<E> {

    void create(E model);

    void update(E model);

    void patch(E model);

    void delById(ID id);

    void delInBatchById(Iterable<ID> id);
}

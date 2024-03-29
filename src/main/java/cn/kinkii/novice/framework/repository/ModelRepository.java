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

    E create(E model);

    E update(E model);

    E patch(E model);

    void deleteInBatchById(Iterable<ID> id);
}

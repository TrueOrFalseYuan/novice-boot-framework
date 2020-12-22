package cn.kinkii.novice.framework.repository;

import cn.kinkii.novice.framework.entity.Identifiable;
import cn.kinkii.novice.framework.entity.LogicalDeleteable;
import cn.kinkii.novice.framework.utils.KBeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.QuerydslJpaRepository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Date;

@SuppressWarnings({"unused"})
@Slf4j
public class BaseModelRepository<E extends Identifiable<ID>, ID extends Serializable> extends QuerydslJpaRepository<E, ID> implements ModelRepository<E, ID> {

    private final EntityManager entityManager;
    private final JpaEntityInformation<E, ID> entityInformation;
    private final Class<E> domainClass;

    @SuppressWarnings("unchecked")
    public BaseModelRepository(JpaEntityInformation<E, ID> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
        this.entityInformation = entityInformation;
        this.domainClass = getDomainClass();
    }

    @Override
    @Transactional
    public void delete(E entity) {
        if (!LogicalDeleteable.class.isAssignableFrom(getDomainClass())) {
            super.delete(entity);
        } else {
            this.logicalDelete(entity);
        }
    }

    @Override
    @Transactional
    public void deleteById(ID id) {
        if (!LogicalDeleteable.class.isAssignableFrom(getDomainClass())) {
            super.deleteById(id);
        } else {
            this.logicalDeleteById(id);
        }
    }

    @Override
    @Transactional
    public void deleteInBatch(Iterable<E> entities) {
        if (!LogicalDeleteable.class.isAssignableFrom(getDomainClass())) {
            super.deleteInBatch(entities);
        } else {
            Assert.notNull(entities, "The given Iterable of entities not be null!");
            entities.forEach(this::logicalDelete);
        }
    }

    @Override
    @Transactional
    public void deleteInBatchById(Iterable<ID> ids) {
        Assert.notNull(ids, "The given Iterable of entity ids not be null!");
        if (LogicalDeleteable.class.isAssignableFrom(getDomainClass())) {
            entityManager.createQuery(createBatchLogicalDeleteCriteria(ids)).executeUpdate();
        } else {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaDelete<E> criteriaDelete = cb.createCriteriaDelete(domainClass);
            Root<E> root = criteriaDelete.from(domainClass);

            CriteriaBuilder.In<Object> inClause = cb.in(root.get(entityInformation.getIdAttribute()));
            ids.forEach(inClause::value);
            criteriaDelete.where(inClause);

            entityManager.createQuery(criteriaDelete).executeUpdate();
        }
    }

    @Override
    @Transactional
    public void deleteAllInBatch() {
        if (!LogicalDeleteable.class.isAssignableFrom(getDomainClass())) {
            super.deleteAllInBatch();
        } else {
            entityManager.createQuery(createBatchLogicalDeleteCriteria()).executeUpdate();
        }
    }

    @Override
    @Transactional
    public E create(E model) {
        return this.saveAndFlush(model);
    }

    @Override
    @Transactional
    public E update(E model) {
        return this.saveAndFlush(model);
    }

    @Override
    @Transactional
    public E patch(E model) {
        E entity = this.findById(model.getId()).orElseThrow(() ->
                new EmptyResultDataAccessException(String.format("No %s entity with id %s exists!", entityInformation.getJavaType(), model.getId()), 1)
        );
        KBeanUtils.copyPropertiesIgnoreNull(model, entity);
        return super.save(entity);
    }

    private void logicalDelete(E entity) {
        logicalDeleteById(entity.getId());
    }

    private void logicalDeleteById(ID id) {
        entityManager.createQuery(createLogicalDeleteCriteria(id)).executeUpdate();
    }

    private CriteriaUpdate<E> createBatchLogicalDeleteCriteria() {
        try {
            LogicalDeleteable target = (LogicalDeleteable) domainClass.newInstance();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaUpdate<E> criteriaUpdate = cb.createCriteriaUpdate(domainClass);
            Root<E> root = criteriaUpdate.from(domainClass);
            criteriaUpdate.set(target.getDelFlag(), true);
            if (StringUtils.hasText(target.getDelTimeFlag())) {
                criteriaUpdate.set(target.getDelTimeFlag(), new Date());
            }
            return criteriaUpdate;
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("Failed to build criteria for logical delete!", e);
            throw new IllegalStateException(e.getMessage());
        }
    }

    private CriteriaUpdate<E> createBatchLogicalDeleteCriteria(Iterable<ID> ids) {
        if (ids == null) {
            throw new IllegalArgumentException("The id can't be null!");
        }
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<E> criteriaUpdate = createBatchLogicalDeleteCriteria();
        Root<E> root = criteriaUpdate.from(domainClass);
        CriteriaBuilder.In<Object> inClause = cb.in(root.get(entityInformation.getIdAttribute()));
        ids.forEach(inClause::value);
        criteriaUpdate.where(inClause);
        return criteriaUpdate;
    }

    private CriteriaUpdate<E> createLogicalDeleteCriteria(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("The id can't be null!");
        }
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<E> criteriaUpdate = createBatchLogicalDeleteCriteria();
        Root<E> root = criteriaUpdate.from(domainClass);
        criteriaUpdate.where(cb.equal(root.get(entityInformation.getIdAttribute()), id));
        return criteriaUpdate;
    }

}

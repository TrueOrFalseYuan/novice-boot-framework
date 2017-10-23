package cn.kinkii.noviceboot.framework.repository;

import cn.kinkii.noviceboot.framework.entity.Identifiable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import java.io.Serializable;


public class BaseModelRepository<E extends Identifiable<ID>, ID extends Serializable> extends SimpleJpaRepository<E, ID> implements ModelRepository<E, ID> {

    private final EntityManager entityManager;

    public BaseModelRepository(JpaEntityInformation<E, ID> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public <T extends E> T create(T newModel) {
        return null;
    }

    @Override
    public <T extends E> T update(T newModel) {
        return null;
    }

    @Override
    public <T extends E> T patch(T newModelData) {
        return null;
    }
}

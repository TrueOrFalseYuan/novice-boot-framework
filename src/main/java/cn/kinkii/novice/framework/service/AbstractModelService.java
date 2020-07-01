package cn.kinkii.novice.framework.service;

import cn.kinkii.novice.framework.entity.Identifiable;
import cn.kinkii.novice.framework.repository.ModelRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public abstract class AbstractModelService<E extends Identifiable<ID>, ID extends Serializable> implements ModelService<E, ID> {

    protected abstract ModelRepository<E, ID> getRepository();

    @Override
    public Optional<E> findById(ID id) {
        return getRepository().findById(id);
    }

    @Override
    public List<E> findAll() {
        return getRepository().findAll();
    }

    @Override
    public Page<E> findAll(Pageable id) {
        return getRepository().findAll(id);
    }

    @Override
    public boolean existsById(ID id) {
        return getRepository().existsById(id);
    }

    @Override
    public void create(E model) {
        getRepository().create(model);
    }

    @Override
    public void update(E model) {
        getRepository().update(model);
    }

    @Override
    public void patch(E model) {
        getRepository().patch(model);
    }

    @Override
    public void delete(E model) {
        getRepository().delete(model);
    }

    @Override
    public void deleteById(ID id) {
        getRepository().deleteById(id);
    }

    @Override
    public void deleteInBatch(Iterable<E> entities) {
        getRepository().deleteInBatch(entities);
    }

    @Override
    public void deleteInBatchById(Iterable<ID> ids) {
        getRepository().deleteInBatchById(ids);
    }
}

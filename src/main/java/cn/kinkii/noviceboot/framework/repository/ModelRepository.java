package cn.kinkii.noviceboot.framework.repository;

import cn.kinkii.noviceboot.framework.entity.Identifiable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface ModelRepository<E extends Identifiable<ID>, ID extends Serializable> extends JpaRepository<E, ID>, JpaSpecificationExecutor<E> {

    <T extends E> T create(T newModel);

    <T extends E> T update(T newModel);

    <T extends E> T patch(T newModelData);

}

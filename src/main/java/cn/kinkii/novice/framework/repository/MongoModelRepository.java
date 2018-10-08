package cn.kinkii.novice.framework.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MongoModelRepository<T, ID> extends MongoRepository<T, ID>{

    List<T> findAll(Query query);

    Page<T> findAll(Query query, Pageable pageable);
}

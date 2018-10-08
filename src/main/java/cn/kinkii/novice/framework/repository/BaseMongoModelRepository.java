package cn.kinkii.novice.framework.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import java.util.List;

public class BaseMongoModelRepository<T, ID> extends SimpleMongoRepository<T, ID> implements MongoModelRepository<T, ID> {

    private final MongoOperations mongoOperations;
    private final MongoEntityInformation<T, ID> entityInformation;

    public BaseMongoModelRepository(MongoEntityInformation<T, ID> metadata, MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.entityInformation = metadata;
        this.mongoOperations = mongoOperations;
    }

    @Override
    public List<T> findAll(Query query) {
        return mongoOperations.find(query, entityInformation.getJavaType());
    }

    @Override
    public Page<T> findAll(Query query, Pageable pageable) {
        query.with(pageable);
        List<T> list = mongoOperations.find(query, entityInformation.getJavaType());
        long total = 0L;
        return new PageImpl<T>(list, pageable, total);
    }

}

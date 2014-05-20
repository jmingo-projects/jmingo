package com.mingo.repository.impl;

import com.mingo.MingoTemplate;
import com.mingo.domain.BaseDocument;
import com.mingo.query.Criteria;
import com.mingo.repository.api.IBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

public abstract class AbstractRepository<T extends BaseDocument> implements IBaseRepository<String, T> {

    @Autowired
    protected MongoTemplate mongoTemplate;
    @Autowired
    protected MingoTemplate mingoTemplate;

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public MingoTemplate getMingoTemplate() {
        return mingoTemplate;
    }

    @Override
    public String insert(T object) {
        mingoTemplate.insert(object);
        return object.getId();
    }

    public void insert(T ...objects) {
        mingoTemplate.insert(objects);
    }

    @Override
    public T findById(String id) {
        return mingoTemplate.findById(id, getDocumentType());
    }

    protected abstract Class<T> getDocumentType();

    @Override
    public List<T> findAll() {
        return mingoTemplate.findAll(getDocumentType());
    }

    @Override
    public void update(T object) {
        Criteria criteria = Criteria.whereId(object.getId());
        mingoTemplate.update(object, criteria);
    }

    @Override
    public void delete(T object) {
        mingoTemplate.remove(object);
    }
}

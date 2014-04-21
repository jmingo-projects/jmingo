package com.mingo.repository.impl;

import com.mingo.MingoTemplate;
import com.mingo.domain.Domain;
import com.mingo.repository.api.IBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

/**
 * Created by dmgcodevil on 21.04.2014.
 */
public abstract class MingoAbstractRepository<T extends Domain>
        implements IBaseRepository<String, T> {

    @Autowired
    private MingoTemplate mingoTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    private String collectionName;

    public MingoAbstractRepository(String collectionName) {
        this.collectionName = collectionName;
    }

    public MingoTemplate getMingoTemplate() {
        return mingoTemplate;
    }

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public String getCollectionName() {
        return collectionName;
    }

    @Override
    public String insert(T object) {
        mongoTemplate.save(object);
        return object.getId();
    }

    @Override
    public T findById(String id) {
        return null;
    }

    @Override
    public List<T> findAll() {
        return mingoTemplate.findAll(getEntityClass(), collectionName);
    }

    @Override
    public void update(T object) {

    }

    @Override
    public void delete(T object) {

    }

    abstract Class<T> getEntityClass();
}

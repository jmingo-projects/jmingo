package com.mingo.demo.repository

import com.mingo.MingoTemplate
import com.mingo.demo.domain.BaseDocument
import com.mingo.demo.repository.api.IBaseRepository
import com.mingo.query.Criteria
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractRepository<T extends BaseDocument> implements IBaseRepository<String, T> {

    protected MingoTemplate mingoTemplate;

    MingoTemplate getMingoTemplate() {
        return mingoTemplate
    }

    @Autowired
    void setMingoTemplate(MingoTemplate mingoTemplate) {
        this.mingoTemplate = mingoTemplate
    }


    @Override
    String insert(T object) {
        mingoTemplate.insert(object);
        return object.getId();
    }

    public void insert(T... objects) {
        mingoTemplate.insert(objects);
    }

    @Override
    T findById(String id) {
        return mingoTemplate.findById(id, getDocumentType());
    }

    protected abstract Class<T> getDocumentType();

    @Override
    List<T> findAll() {
        return mingoTemplate.findAll(getDocumentType());
    }

    @Override
    void update(T object) {
        Criteria criteria = Criteria.whereId(object.getId());
        mingoTemplate.update(object, criteria);
    }

    @Override
    void delete(T object) {
        mingoTemplate.remove(object);
    }
}
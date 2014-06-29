package org.jmingo.demo.repository

import org.jmingo.JMingoTemplate
import org.jmingo.demo.domain.BaseDocument
import org.jmingo.query.Criteria
import org.jmingo.demo.repository.api.IBaseRepository
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractRepository<T extends BaseDocument> implements IBaseRepository<String, T> {

    @Autowired
    protected JMingoTemplate jMingoTemplate;

    JMingoTemplate getJMingoTemplate() {
        return jMingoTemplate
    }

    @Override
    String insert(T object) {
        jMingoTemplate.insert(object);
        return object.getId();
    }

    public void insert(T... objects) {
        jMingoTemplate.insert(objects);
    }

    @Override
    T findById(String id) {
        return jMingoTemplate.findById(id, getDocumentType());
    }

    protected abstract Class<T> getDocumentType();

    @Override
    List<T> findAll() {
        return jMingoTemplate.findAll(getDocumentType());
    }

    @Override
    void update(T object) {
        Criteria criteria = Criteria.whereId(object.getId());
        jMingoTemplate.update(object, criteria);
    }

    @Override
    void delete(T object) {
        jMingoTemplate.remove(object);
    }
}
package com.mingo.repository.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;


import com.mingo.domain.Domain;
import com.mingo.repository.api.IBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.UUID;

/**
 * Copyright 2012-2013 The Mingo Team
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @param <ID> The type of unique identifier.
 * @param <T>  The type of objects managed by repository.
 */
public abstract class AbstractBaseRepository<ID, T extends Domain>
    implements IBaseRepository<ID, T> {

    /**
     * FIRST ELEMENT.
     */
    public static final int FIRST_ELEMENT = 0;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Gets mongo template object.
     *
     * @return mongo template.
     */
    protected MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    /**
     * Sets mongo template object.
     *
     * @param mongoTemplate {@link MongoTemplate}
     */
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ID insert(T document) {
        document.setId(UUID.randomUUID().toString());
        mongoTemplate.insert(document);
        return (ID) document.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(T document) {
        mongoTemplate.remove(document);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T findById(ID id) {
        return mongoTemplate.findOne(query(where("_id").is(id)), getDomainClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(T document) {
        mongoTemplate.save(document);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findAll() {
        return (List<T>) getMongoTemplate().findAll(getDomainClass());
    }

    /**
     * Gets domain object class.
     *
     * @return domain object class
     */
    protected abstract Class<? extends T> getDomainClass();
}

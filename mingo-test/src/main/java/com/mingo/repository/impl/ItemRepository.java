package com.mingo.repository.impl;

import com.mingo.MingoTemplate;
import com.mingo.domain.Item;
import com.mingo.query.Criteria;
import com.mingo.repository.api.IBaseRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class ItemRepository extends AbstractRepository<Item> implements IBaseRepository<String, Item> {

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public MingoTemplate getMingoTemplate() {
        return mingoTemplate;
    }

    @Override
    protected Class<Item> getDocumentType() {
        return Item.class;
    }


    public List<Item> findByName(String name) {
        Criteria criteria = Criteria.where("{name : '#name'}").with("name", name);
        return mingoTemplate.find(criteria, Item.class);
    }

    public List<Item> findAfterDate(Date date) {
        Criteria criteria = Criteria
                .where("{ 'date' : { '$gt' : '#date'} }")
                .with("date", date);
        return mingoTemplate.find(criteria, Item.class);
    }


    public void updateByName(Item item, String name) {
        Criteria criteria = Criteria.where("{name : '#name'}").with("name", name);
        mingoTemplate.update(item, criteria, Item.class);
    }


    public void deleteByName(String name) {
        Criteria criteria = Criteria.where("{name : '#name'}").with("name", name);
        mingoTemplate.remove(criteria, Item.class);
    }
}

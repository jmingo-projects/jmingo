package com.mingo.repository.impl;

import com.google.common.collect.Lists;
import com.mingo.MingoTemplate;
import com.mingo.domain.Item;
import com.mingo.query.Criteria;
import com.mingo.repository.api.IBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public class ItemRepository implements IBaseRepository<String, Item> {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private MingoTemplate mingoTemplate;

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public MingoTemplate getMingoTemplate() {
        return mingoTemplate;
    }

    @Override
    public String insert(Item item) {
        mingoTemplate.insert(item);
        return item.getId();
    }

    @Override
    public Item findById(String id) {
        return mingoTemplate.findById(id, Item.class);
    }

    public List<Item> findByName(String name){
        Criteria criteria = Criteria.where("{name : '#name'}").with("name", name);
        return mingoTemplate.find(criteria, Item.class);
    }

    public List<Item> findAfterDate(Date date) {
        Criteria criteria = Criteria
                .where("{ 'date' : { '$gt' : '#date'} }")
                .with("date", date);
        return mingoTemplate.find(criteria, Item.class);
    }

    /**
     * works through mingo template.
     */
    @Override
    public List<Item> findAll() {
        return mingoTemplate.findAll(Item.class);
    }

    @Override
    public void update(Item item) {
        Criteria criteria = Criteria.whereId(item.getId());
        mingoTemplate.update(item, criteria, Item.class);
    }

    public void updateByName(Item item, String name) {
        Criteria criteria = Criteria.where("{name : '#name'}").with("name", name);
        mingoTemplate.update(item, criteria, Item.class);
    }

    @Override
    public void delete(Item object) {
        mingoTemplate.remove(object);
    }

    public void deleteByName(String name){
        Criteria criteria = Criteria.where("{name : '#name'}").with("name", name);
        mingoTemplate.remove(criteria, Item.class);
    }
}

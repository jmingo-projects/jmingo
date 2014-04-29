package com.mingo.repository.impl;

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

    //todo swith to mingo template
    @Override
    public void delete(Item object) {

    }
}

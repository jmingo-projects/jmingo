package com.mingo.repository.impl;

import com.mingo.domain.Item;
import org.springframework.stereotype.Repository;

/**
 * Created by dmgcodevil on 21.04.2014.
 */
@Repository
public class ItemRepository extends MingoAbstractRepository<Item>  {



    public ItemRepository() {
        super("item");
    }

    @Override
    Class<Item> getEntityClass() {
        return Item.class;
    }
}

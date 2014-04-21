package com.mingo.integ.repository;

import com.google.common.collect.Sets;
import com.mingo.domain.Item;
import com.mingo.repository.impl.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.Set;
import java.util.UUID;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Created by dmgcodevil on 21.04.2014.
 */
public class ItemRepositoryIntegrationTest extends CommonIntegrationTest{

    @Autowired
    private ItemRepository itemRepository;


   @Test
   public void testFindAll(){
       itemRepository.getMingoTemplate().dropCollection("item");
       Item item1 = new Item("1");
       Item item2 = new Item("2");
       Item item3 = new Item("3");
       Set<Item> items = Sets.newHashSet();
       items.add(item1);
       items.add(item2);
       items.add(item3);
       item1.setId(itemRepository.insert(new Item("1")));
       item2.setId(itemRepository.insert(new Item("2")));
       item3.setId(itemRepository.insert(new Item("3")));
       Set<Item> saved = Sets.newHashSet(itemRepository.getMingoTemplate().findAll(Item.class, "item"));
       assertEquals(saved, items);
    }
}

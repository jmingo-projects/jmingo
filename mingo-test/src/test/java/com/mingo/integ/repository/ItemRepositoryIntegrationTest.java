package com.mingo.integ.repository;

import com.google.common.collect.Sets;
import com.mingo.domain.Item;
import com.mingo.repository.impl.ItemRepository;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.Set;

import static org.testng.AssertJUnit.assertEquals;


public class ItemRepositoryIntegrationTest extends CommonIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;


    @Test(groups = "integration")
    public void testFindAll() {
        Date date = new Date();
        itemRepository.getMingoTemplate().dropCollection(Item.class);
        Item item1 = new Item("1");
        item1.setDate(date);
        Item item2 = new Item("2");
        item2.setDate(date);
        Item item3 = new Item("3");
        item3.setDate(date);
        Set<Item> items = Sets.newHashSet(item1, item2, item3);
        item1.setId(itemRepository.insert(item1));
        item2.setId(itemRepository.insert(item2));
        item3.setId(itemRepository.insert(item3));
        Set<Item> saved = Sets.newHashSet(itemRepository.getMingoTemplate().findAll(Item.class));
        assertEquals(saved, items);
    }

    @Test(groups = "integration")
    public void testUpdateById() {
        itemRepository.getMingoTemplate().dropCollection(Item.class);
        Item source = new Item("source");
        source.setDate(new Date());
        itemRepository.insert(source);
        source.setName("new");
        itemRepository.update(source);
        Item updated = itemRepository.findById(source.getId());
        assertEquals(updated, source);
    }

    @Test(groups = "integration")
    public void testFindAfter() {
        itemRepository.getMingoTemplate().dropCollection(Item.class);
        Item item1 = new Item("1");
        item1.setDate(DateUtils.addDays(new Date(), -1));
        Item item2 = new Item("2");
        item2.setDate(DateUtils.addDays(new Date(), -2));
        item1.setId(itemRepository.insert(item1));
        item2.setId(itemRepository.insert(item2));

        Set<Item> expected = Sets.newHashSet(item1);
        //Set<Item> saved = Sets.newHashSet(itemRepository.findAll());
       Set<Item> saved = Sets.newHashSet(itemRepository.findAfterDate(DateUtils.addDays(new Date(), -2)));
        assertEquals(expected, saved);

    }
}

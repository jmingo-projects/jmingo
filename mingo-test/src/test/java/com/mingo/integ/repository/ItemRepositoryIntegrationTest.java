package com.mingo.integ.repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mingo.domain.Item;
import com.mingo.repository.impl.ItemRepository;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;


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
        assertEquals(updated.getName(), source.getName());
        assertEquals(updated.getDate(), source.getDate());
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
        Set<Item> saved = Sets.newHashSet(itemRepository.findAfterDate(DateUtils.addDays(new Date(), -2)));
        assertEquals(saved, expected);
    }

    @Test(groups = "integration")
    public void testUpdateByName() {
        itemRepository.getMingoTemplate().dropCollection(Item.class);
        //given
        Item item = new Item("1");
        String name = "testItem";
        item.setName(name);
        //then
        itemRepository.insert(item);
        item.setDate(new Date());
        String id = item.getId();
        assertNotNull(id);
        Item saved = itemRepository.findById(id);
        assertNull(saved.getDate());
        itemRepository.updateByName(item, name);
        saved = itemRepository.findById(id);
        assertEquals(saved.getDate(), item.getDate());
    }

    @Test(groups = "integration")
    public void testDelete() {
        itemRepository.getMingoTemplate().dropCollection(Item.class);
        //given
        Item item = new Item("testItem");
        item.setDate(new Date());
        //then
        itemRepository.insert(item);
        String id = item.getId();
        Item saved = itemRepository.findById(id);
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertNotNull(saved.getName());
        assertNotNull(saved.getDate());

        itemRepository.delete(item);
        saved = itemRepository.findById(id);
        assertNull(saved);
    }

    @Test(groups = "integration")
    public void testDeleteByName() {
        itemRepository.getMingoTemplate().dropCollection(Item.class);
        String sameName = "same_name";
        Item item1 = new Item(sameName);
        item1.setDate(new Date());
        Item item2 = new Item(sameName);
        item2.setDate(new Date());
        Item item3 = new Item(sameName);
        item3.setDate(new Date());
        List<Item> items = Lists.newArrayList(item1, item2, item3);
        itemRepository.insert(item1);
        itemRepository.insert(item2);
        itemRepository.insert(item3);
        List<Item> saved = itemRepository.findByName(sameName);
        assertNotNull(saved);
        assertEquals(saved.size(), items.size());
        itemRepository.deleteByName(sameName);
        saved = itemRepository.findByName(sameName);
        assertNotNull(saved);
        assertEquals(saved.size(), 0);
    }

}

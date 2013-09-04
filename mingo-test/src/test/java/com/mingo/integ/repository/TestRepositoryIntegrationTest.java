package com.mingo.integ.repository;

import com.mingo.domain.Item;
import com.mingo.repository.api.ITestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestRepositoryIntegrationTest extends CommonIntegrationTest {

    private static final String TEST_NAME = "hello mingo";
    private static final String ITEM_NAME = "new item";
    @Autowired
    private ITestRepository testRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private List<com.mingo.domain.Test> testList;

    @BeforeClass(groups = "integration")
    public void setUp() {
        mongoTemplate.dropCollection(Test.class);
        testList = new ArrayList<>();
        com.mingo.domain.Test test1 = createTest(UUID.randomUUID().toString(), "");
        com.mingo.domain.Test test2 = createTest(UUID.randomUUID().toString(), "");
        testRepository.insert(test1);
        testRepository.insert(test2);
        testList.add(test1);
        testList.add(test2);
    }

    @Test(groups = "integration")
    public void getById() {
        com.mingo.domain.Test currentTest = testList.get(0);
        com.mingo.domain.Test savedTest = testRepository.getById(currentTest.getId());
        Assert.assertNotNull(savedTest);
        Assert.assertEquals(savedTest.getIdentifier(), currentTest.getIdentifier());
    }

    @Test(groups = "integration")
    public void getByIdentifier() {
        com.mingo.domain.Test currentTest = testList.get(0);
        com.mingo.domain.Test savedTest = testRepository.getByIdentifier(currentTest.getIdentifier());
        Assert.assertNotNull(savedTest);
        Assert.assertEquals(savedTest.getIdentifier(), currentTest.getIdentifier());
    }

    @Test(groups = "integration")
    public void testSaveAndReadByName() {
        List<com.mingo.domain.Test> savedTestList = testRepository.getByName(TEST_NAME);
        Assert.assertEquals(savedTestList.size(), 2);
    }

    @Test(groups = "integration", expectedExceptions = RuntimeException.class)
    public void testSaveAndReadByEmptyName() {
        List<com.mingo.domain.Test> savedTestList = testRepository.getByName("");
        Assert.assertNull(savedTestList);
    }

    private com.mingo.domain.Test createTest(String identifier, String suffix) {
        com.mingo.domain.Test test = new com.mingo.domain.Test(TEST_NAME + suffix);
        test.setIdentifier(identifier);
        test.addItem(new Item(ITEM_NAME + suffix));
        return test;
    }
}

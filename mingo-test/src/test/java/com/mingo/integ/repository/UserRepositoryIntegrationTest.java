package com.mingo.integ.repository;

import com.mingo.domain.User;
import com.mingo.repository.api.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class UserRepositoryIntegrationTest extends CommonIntegrationTest {

    @Autowired
    private IUserRepository userRepository;


    @Autowired
    private MongoTemplate mongoTemplate;
    private User user;

    @BeforeClass(groups = "integration")
    public void setUp() {
        mongoTemplate.dropCollection(User.class);
        user = new User("test user");
        userRepository.insert(user);
    }

    @Test(groups = "integration")
    public void testGetByName() {
        User repUser = userRepository.getByName("test user");
        Assert.assertNotNull(repUser);
        Assert.assertEquals(user.getId(), repUser.getId());
        Assert.assertEquals("UserConverter::" + user.getName(), repUser.getName());
    }

}

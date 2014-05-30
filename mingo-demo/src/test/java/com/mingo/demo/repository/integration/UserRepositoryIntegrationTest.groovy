package com.mingo.demo.repository.integration

import com.mingo.demo.domain.Person;
import com.mingo.demo.domain.User;
import com.mingo.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertEquals;

@ContextConfiguration(locations = "classpath:META-INF/spring/applicationContext.xml")
public class UserRepositoryIntegrationTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private UserRepository userRepository;

    @BeforeMethod
    public void setUp() throws Exception {
        userRepository.getMingoTemplate().removeAll(User.class);
    }

    @Test
    public void testInsertUser() {
        User user = createUser();
        userRepository.insert(user);
        User saved = userRepository.findById(user.getId());
        assertNotNull(saved);
        assertEquals(saved.id, user.id);
        assertEquals(saved.login, user.login);
        assertEquals(saved.password, user.password);
        assertEquals(saved.person.firstName, user.person.firstName);
        assertEquals(saved.person.secondName, user.person.secondName);
        assertEquals(saved.person.age, user.person.age);
        assertEquals(saved.person.birth, user.person.birth);
        assertEquals(saved.person.email, user.person.email);
    }

    User createUser() {
        def user = new User()
        user.login = 'test'
        user.password = 'password'
        user.person = new Person(firstName: 'Jeff', secondName: 'Waters',
                birth: new Date(), age: 24, email: 'test@email.com')
        return user
    }
}






package org.jmingo.demo.repository.integration

import org.jmingo.demo.domain.Group
import org.jmingo.demo.domain.Person
import org.jmingo.demo.domain.User
import org.jmingo.demo.repository.UserRepository
import org.apache.commons.lang3.time.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertNull

@ContextConfiguration(locations = "classpath:META-INF/spring/applicationContext.xml")
public class UserRepositoryIntegrationTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private UserRepository userRepository;

    @BeforeMethod
    public void setUp() throws Exception {
        userRepository.getJMingoTemplate().removeAll(User.class);
    }

    @Test
    public void testInsertUser() {
        User user = createUser();
        userRepository.insert(user);
        User saved = userRepository.findById(user.getId());
        assertNotNull(saved);
    }

    @Test
    public void testFindUserById() {
        User user = createUser();
        userRepository.insert(user);
        User saved = userRepository.findById(user.getId());
        assertNotNull(saved);
        assertUserEquals(saved, user);
    }

    @Test
    public void testUpdateUser() {
        User user = createUser();
        userRepository.insert(user);
        user.person.email = 'new_email@mail.com';
        user.password = 'new_password';
        userRepository.update(user);
        User saved = userRepository.findById(user.getId());
        assertNotNull(saved);
        assertUserEquals(saved, user);
    }

    @Test
    public void testDeleteUserById() {
        User user = createUser();
        userRepository.insert(user);
        User saved = userRepository.findById(user.getId());
        assertNotNull(saved);
        userRepository.delete(user);
        saved = userRepository.findById(user.getId());
        assertNull(saved);
    }

    @Test
    public void testFindAll() {
        User user1 = createUser();
        User user2 = createUser();
        user2.login = 'login2'
        userRepository.insert(user1, user2)
        def saved = userRepository.findAll()
        assertNotNull(saved)
        assertEquals(saved.size(), 2)
    }

    @Test
    void testFindByBirthday() {
        User user1 = createUser('login1')
        user1.person.birthday = DateUtils.addDays(new Date(), -6)
        User user2 = createUser('login2')
        user2.person.birthday = DateUtils.addDays(new Date(), -4)
        User user3 = createUser('login3')
        user3.person.birthday = DateUtils.addDays(new Date(), -2)
        userRepository.insert(user1, user2, user3)
        def saved = userRepository.findByBirthday(DateUtils.addDays(new Date(), -5),
                DateUtils.addDays(new Date(), -3))
        assertNotNull(saved)
        assertEquals(saved.size(), 1)
    }

    @Test
    void testFindByLogin() {
        User user = createUser('login')
        userRepository.insert(user)
        User saved = userRepository.findByLogin('login')
        assertNotNull saved
        assertUserEquals saved, user
    }

    @Test
    void testFindByAccounts() {
        User user1 = createUser('login1')
        user1.accounts = ['google', 'twitter', 'facebook']
        User user2 = createUser('login2')
        user2.accounts = ['dropbox']
        User user3 = createUser('login3')
        user3.accounts = ['facebook']
        userRepository.insert(user1, user2, user3)
        def saved = userRepository.findByAccounts(['google', 'twitter', 'facebook'])
        assertNotNull(saved)
        assertEquals(saved.size(), 2)
    }

    @Test
    void testFindByGroupName() {
        User user1 = createUser('login1')
        user1.groups = [new Group(name: 'java'), new Group(name: 'groovy')]
        User user2 = createUser('login2')
        user2.groups = [new Group(name: 'scala')]
        userRepository.insert(user1, user2)
        def saved = userRepository.findByGroupName('groovy')
        assertNotNull(saved)
        assertEquals(saved.size(), 1)
        assertUserEquals(saved.get(0), user1)
    }

    private void assertUserEquals(User actual, User expected) {
        assertEquals(actual.id, expected.id);
        assertEquals(actual.login, expected.login);
        assertEquals(actual.password, expected.password);
        assertEquals(actual.person.firstName, expected.person.firstName);
        assertEquals(actual.person.secondName, expected.person.secondName);
        assertEquals(actual.person.age, expected.person.age);
        assertEquals(actual.person.birthday, expected.person.birthday);
        assertEquals(actual.person.email, expected.person.email);
    }

    User createUser(def login = 'login', def name = 'name') {
        def user = new User()
        user.login = login
        user.password = 'password'
        user.person = new Person(firstName: name, secondName: 'secondName',
                birthday: new Date(), age: 24, email: 'test@email.com')
        return user
    }
}






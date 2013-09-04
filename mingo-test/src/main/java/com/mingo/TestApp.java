package com.mingo;

import com.mingo.domain.Item;
import com.mingo.domain.Test;
import com.mingo.exceptions.ContextInitializationException;
import com.mingo.repository.api.ITestRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Fake test class.
 *
 * @author dmgcodevil
 */
public class TestApp {

    public static void main(String[] args) throws ContextInitializationException {
        ApplicationContext applicationContext =
            new ClassPathXmlApplicationContext("classpath:spring/repository-context.xml");

/*

        ITestRepository testRepository = (ITestRepository) applicationContext.getBean("testRepository");
        MongoTemplate mongoTemplate = (MongoTemplate) applicationContext.getBean("mongoTemplate");
        mongoTemplate.dropCollection(Test.class);
        Test test = createAndSave(testRepository);
        Test savedTest = testRepository.getByName("hello mingo");
*/


//        Context context = ContextLoader.getInstance().load("/mingo/mingo-context.xml");
//        QueryExecutor queryExecutor =  QueryExecutorFactory.create(context);
    }

    private static Test createAndSave(ITestRepository testRepository) {
        Test test = createTest();
        testRepository.insert(test);
        return test;
    }


    private static Test createTest() {
        Test test = new Test("hello mingo");
        test.addItem(new Item("new item"));
        return test;
    }
}

package com.mingo.integ.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

/**
 * Common class for all tests.
 */
@ContextConfiguration(locations = {"classpath:spring/repository-context.xml"})
public class CommonIntegrationTest extends AbstractTestNGSpringContextTests {

    @Autowired
    protected MongoTemplate mongoTemplate;

}

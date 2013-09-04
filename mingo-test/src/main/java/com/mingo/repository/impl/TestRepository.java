package com.mingo.repository.impl;

import com.google.common.collect.ImmutableMap;
import com.mingo.core.MingoTemplate;
import com.mingo.domain.Test;
import com.mingo.executor.QueryExecutor;
import com.mingo.repository.api.ITestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import javax.annotation.PostConstruct;

/**
 * Copyright 2012-2013 The Mingo Team
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@Repository("testRepository")
public class TestRepository extends AbstractBaseRepository<String, Test>
    implements ITestRepository {


    @Autowired
    private MingoTemplate mingoTemplate;

    @PostConstruct
    private void postConstruct() {
       /* queryExecutor.registerConverter(getDomainClass(),
            new BaseMongoConverter(getMongoTemplate().getConverterClass()));*/
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<? extends Test> getDomainClass() {
        return Test.class;
    }

    @Override
    public List<Test> getByName(String name) {
        return (List<Test>) mingoTemplate.queryForList("mingotest.test.getTestByName", getDomainClass(),
            ImmutableMap.<String, Object>of("name", name));

    }

    @Override
    public Test getByIdentifier(String identifier) {
        return mingoTemplate.queryForObject("mingotest.test.getTestByIdentifier", getDomainClass(),
            ImmutableMap.<String, Object>of("identifier", identifier));
    }

    @Override
    public Test getById(String id) {
        return mingoTemplate.queryForObject("mingotest.test.getTestById", getDomainClass(),
            ImmutableMap.<String, Object>of("id", id));
    }
}

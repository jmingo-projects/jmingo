package com.mingo.repository.impl;

import com.google.common.collect.ImmutableMap;
import com.mingo.core.MingoTemplate;
import com.mingo.domain.User;
import com.mingo.executor.QueryExecutor;
import com.mingo.repository.api.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Service("userRepository")
public class UserRepository extends AbstractBaseRepository<String, User>
    implements IUserRepository {

    @Autowired
    private MingoTemplate mingoTemplate;


    @Override
    public User getByName(String name) {
        return mingoTemplate.queryForObject("mingotest.user.getByName", getDomainClass(),
            ImmutableMap.<String, Object>of("name", name));
    }

    @Override
    protected Class<? extends User> getDomainClass() {
        return User.class;
    }
}

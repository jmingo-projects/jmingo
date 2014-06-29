/**
 * Copyright 2013-2014 The JMingo Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jmingo.document.id.generator.factory;


import com.google.common.collect.Maps;
import org.jmingo.document.id.generator.IdGenerator;
import org.jmingo.document.id.generator.IdGeneratorStrategy;
import org.jmingo.document.id.generator.ObjectIdGenerator;
import org.jmingo.document.id.generator.SnowflakeGenerator;
import org.jmingo.document.id.generator.UUIDGenerator;
import org.bson.types.ObjectId;

import java.util.Map;

/**
 * Implementation of {@link IdGeneratorFactory}.
 */
public class DefaultIdGeneratorFactory implements IdGeneratorFactory {

    private Map<String, IdGenerator> generators = Maps.newHashMap();
    private Map<Class<?>, IdGenerator> generatorsBindingTypes = Maps.newHashMap();


    public DefaultIdGeneratorFactory() {
        register();
    }

    private void register() {
        IdGenerator uuidGenerator = new UUIDGenerator();
        IdGenerator objectIdGenerator = new ObjectIdGenerator();
        IdGenerator snowflakeGenerator = new SnowflakeGenerator();
        register(IdGeneratorStrategy.OBJECT_ID, objectIdGenerator);
        register(IdGeneratorStrategy.UUID, uuidGenerator);
        register(IdGeneratorStrategy.SNOWFLAKE, snowflakeGenerator);
        //don't register IdGeneratorStrategy.TYPE because it's used to find generator by field type

        register(ObjectId.class, objectIdGenerator);
        register(String.class, uuidGenerator);
        register(Long.class, snowflakeGenerator);
        register(Long.TYPE, snowflakeGenerator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRegistered(String strategy) {
        return generators.containsKey(strategy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRegistered(Class<?> type) {
        return generatorsBindingTypes.containsKey(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(String strategy, IdGenerator idGenerator) {
        if (!IdGeneratorStrategy.TYPE.equalsIgnoreCase(strategy)) {
            generators.put(strategy, idGenerator);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(Class<?> type, IdGenerator idGenerator) {
        generatorsBindingTypes.put(type, idGenerator);
    }

    /**
     * @param strategy the id generation strategy
     * @param type     the type of id field to generate value. if strategy is
     *                 {@link org.jmingo.document.id.generator.IdGeneratorStrategy#TYPE} then factory
     *                 tries to find applicable generator based on id field type.
     * @return an implementation of {@link IdGenerator} for specified strategy/type
     */
    @Override
    public IdGenerator create(String strategy, Class<?> type) {
        IdGenerator idGenerator = null;
        if (generators.containsKey(strategy)) {
            idGenerator = generators.get(strategy);
        } else if (IdGeneratorStrategy.TYPE.equalsIgnoreCase(strategy)) {
            idGenerator = generatorsBindingTypes.get(type);
        }
        return idGenerator;
    }

}

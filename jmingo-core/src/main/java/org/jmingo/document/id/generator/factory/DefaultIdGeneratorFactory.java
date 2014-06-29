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
import org.jmingo.exceptions.IdGenerationException;

import java.util.Map;

/**
 * Implementation of {@link IdGeneratorFactory}.
 */
public class DefaultIdGeneratorFactory implements IdGeneratorFactory {

    private Map<String, Class<? extends IdGenerator>> lazyGeneratorRegistry = Maps.newHashMap();
    private Map<String, IdGenerator> generatorRegistry = Maps.newConcurrentMap();

    public DefaultIdGeneratorFactory() {
        register();
    }

    private void register() {
        register(IdGeneratorStrategy.OBJECT_ID, ObjectIdGenerator.class);
        register(IdGeneratorStrategy.UUID, UUIDGenerator.class);
        register(IdGeneratorStrategy.SNOWFLAKE, SnowflakeGenerator.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRegistered(String strategy) {
        return lazyGeneratorRegistry.containsKey(strategy) || generatorRegistry.containsKey(strategy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(String strategy, IdGenerator idGenerator) {
        generatorRegistry.put(strategy, idGenerator);
    }

    private void register(String strategy, Class<? extends IdGenerator> idGenerator) {
        lazyGeneratorRegistry.put(strategy, idGenerator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IdGenerator create(String strategy) {
        return generatorRegistry.computeIfAbsent(strategy, this::load);
    }

    private IdGenerator load(String strategy) {
        IdGenerator idGenerator = null;
        if (lazyGeneratorRegistry.containsKey(strategy)) {
            Class<? extends IdGenerator> generatorClass = lazyGeneratorRegistry.get(strategy);
            try {
                idGenerator = generatorClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IdGenerationException(e);
            }
        }
        return idGenerator;
    }

}

/**
 * Copyright 2012-2013 The Mingo Team
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
package com.mingo.document.id.generator.factory;


import com.google.common.collect.Maps;
import com.mingo.document.id.generator.IdGenerator;
import com.mingo.document.id.generator.IdGeneratorStrategy;
import com.mingo.document.id.generator.ObjectIdGenerator;
import com.mingo.document.id.generator.SnowflakeGenerator;
import com.mingo.document.id.generator.UUIDGenerator;
import org.bson.types.ObjectId;

import java.util.Map;

public class DefaultIdGeneratorFactory implements IdGeneratorFactory {

    private Map<String, IdGenerator> generators = Maps.newHashMap();
    private Map<Class<?>, IdGenerator> generatorsBindingTypes = Maps.newHashMap();

    private void register() {
        IdGenerator uuidGenerator = new UUIDGenerator();
        IdGenerator objectIdGenerator = new ObjectIdGenerator();
        //IdGenerator snowflakeGenerator = SnowflakeGenerator.getInstance();
        register(IdGeneratorStrategy.OBJECT_ID, objectIdGenerator);
        register(IdGeneratorStrategy.UUID, uuidGenerator);
        //register(IdGeneratorStrategy.SNOWFLAKE, snowflakeGenerator);
        //don't register IdGeneratorStrategy.TYPE because it's used to find generator by field type

        register(ObjectId.class, objectIdGenerator);
        register(String.class, uuidGenerator);
        //register(Long.class, snowflakeGenerator);
        //register(Long.TYPE, snowflakeGenerator);
    }

    public DefaultIdGeneratorFactory() {
        register();
    }

    public boolean isRegistered(String strategy) {
        return generators.containsKey(strategy);
    }

    public boolean isRegistered(Class<?> type) {
        return generatorsBindingTypes.containsKey(type);
    }

    public void register(String strategy, IdGenerator idGenerator) {
        generators.putIfAbsent(strategy, idGenerator);
    }

    public void register(Class<?> type, IdGenerator idGenerator) {
        generatorsBindingTypes.putIfAbsent(type, idGenerator);
    }

    public IdGenerator unregister(String strategy) {
        return generators.remove(strategy);
    }

    public IdGenerator unregister(Class<?> type) {
        return generatorsBindingTypes.remove(type);
    }

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

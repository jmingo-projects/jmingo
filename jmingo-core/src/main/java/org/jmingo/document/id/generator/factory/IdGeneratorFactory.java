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


import org.jmingo.document.id.generator.IdGenerator;

/**
 * Factory to create id generators.
 */
public interface IdGeneratorFactory {

    /**
     * Creates instance of {@link IdGenerator} implementation related to the given strategy.
     *
     * @param strategy the id generation strategy
     * @param type     the type of id field to generate value.
     * @return specific implementation of {@link IdGenerator}
     */
    IdGenerator create(String strategy, Class<?> type);

    /**
     * Registers the given generator for the specified strategy.
     *
     * @param strategy    the name of strategy, not necessary to be mentioned in {@link org.jmingo.document.id.generator.IdGeneratorStrategy}
     *                    and can have same name but it's prohibited to use 'type' strategy name
     * @param idGenerator implementation of {@link org.jmingo.document.id.generator.IdGenerator} for the specified strategy
     */
    void register(String strategy, IdGenerator idGenerator);

    /**
     * Registers the given generator for the specified type. Later to create generator for the given type the "type" strategy must used.
     *
     * @param type        the type of id field for which the given generator should be registered
     * @param idGenerator implementation of {@link org.jmingo.document.id.generator.IdGenerator} for the specified type
     */
    void register(Class<?> type, IdGenerator idGenerator);

    /**
     * Checks that a generator already registered for specified strategy.
     *
     * @param strategy the id generation strategy
     * @return true of there is generator for specified strategy, otherwise - false
     */
    boolean isRegistered(String strategy);

    /**
     * Checks that a generator already registered for specified type.
     *
     * @param type the type
     * @return true of there is generator for specified type, otherwise - false
     */
    boolean isRegistered(Class<?> type);
}

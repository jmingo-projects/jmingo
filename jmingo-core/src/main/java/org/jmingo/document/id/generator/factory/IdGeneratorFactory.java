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
     * Creates generator instance for the given strategy or returns the generator instance if already created.
     *
     * @param strategy the generation strategy
     * @return specific implementation of {@link IdGenerator}
     */
    IdGenerator create(String strategy);

    /**
     * Registers the given generator for the given strategy. Replaces registered generator with new if need be. see {@link #isRegistered(String)}.
     *
     * @param strategy    the name of generator strategy
     * @param idGenerator implementation of {@link org.jmingo.document.id.generator.IdGenerator} for the given strategy
     */
    void register(String strategy, IdGenerator idGenerator);

    /**
     * Checks that a generator already registered for specified strategy name.
     *
     * @param strategy the generation strategy
     * @return true of there is registered generator for the given strategy, otherwise - false
     */
    boolean isRegistered(String strategy);
}

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
package com.mingo.document.id.generator;

public class IdGeneratorStrategy {

    /**
     * Using this strategy allows Mingo decide which implementation of {@link com.mingo.document.id.generator.IdGenerator}
     * should be used based on id field value. You can register your own generator for specified type using {@link com.mingo.document.id.generator.factory.IdGeneratorFactory#register(String, IdGenerator)}.
     * You should make sure that all necessary generators are registered for all id types that you're using in your code.
     * Recommended to avoid using 'TYPE' strategy everywhere where it's possible and specify concrete generator strategy
     * to avoid confusion if the generated value is not expected or acceptable. To know which strategies are registered by default for concrete types you can read javadoc for chosen IdGeneratorFactory implementation.
     * Anyway we better to know which IdGenerator implementation is used in a certain case.
     */
    public static final String TYPE = "type";

    /**
     * This strategy uses {@link UUIDGenerator} implementation of {@link com.mingo.document.id.generator.IdGenerator}.
     * This strategy is applied only for {@link java.lang.String} fields thus if you try to apply this generator
     * for Long field then you receive {@link com.mingo.exceptions.IdGenerationException}.
     */
    public static final String UUID = "uuid";

    /**
     * This strategy uses {@link ObjectIdGenerator} implementation of {@link com.mingo.document.id.generator.IdGenerator}.
     */
    public static final String OBJECT_ID = "object_id";

    /**
     * This strategy uses {@link SnowflakeGenerator} implementation of {@link com.mingo.document.id.generator.IdGenerator}.
     */
    public static final String SNOWFLAKE = "snowflake";
}

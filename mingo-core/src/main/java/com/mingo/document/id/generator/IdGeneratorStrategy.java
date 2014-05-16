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
     * Using this strategy allows Mingo decide which implementation should be used based on id field value.
     * Recommended to specify concrete generator strategy and avoid using 'TYPE' strategy everywhere where it  possible
     * to avoid confusion if the generated value is not expected or acceptable.
     * Anyway better know which idGenerator implementation is used in current case.
     */
    public static final String TYPE = "type";

    /**
     * {@link UUIDGenerator} implementation.
     */
    public static final String UUID = "uuid";
    /**
     * {@link ObjectIdGenerator} implementation.
     */
    public static final String OBJECT_ID = "object_id";

    /**
     * {@link SnowflakeGenerator} implementation.
     */
    public static final String SNOWFLAKE = "snowflake";
}

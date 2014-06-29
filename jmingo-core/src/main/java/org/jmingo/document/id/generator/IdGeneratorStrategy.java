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
package org.jmingo.document.id.generator;

public class IdGeneratorStrategy {

    /**
     * This strategy uses {@link UUIDGenerator} implementation of {@link IdGenerator}.
     * This strategy is applied only for {@link java.lang.String} fields thus if you try to apply this generator
     * for Long field then you receive {@link org.jmingo.exceptions.IdGenerationException}.
     */
    public static final String UUID = "uuid";

    /**
     * This strategy uses {@link ObjectIdGenerator} implementation of {@link IdGenerator}.
     */
    public static final String OBJECT_ID = "object_id";

    /**
     * This strategy uses {@link SnowflakeGenerator} implementation of {@link IdGenerator}.
     */
    public static final String SNOWFLAKE = "snowflake";
}

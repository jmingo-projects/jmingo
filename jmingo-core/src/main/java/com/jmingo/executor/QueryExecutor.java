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
package com.jmingo.executor;

import java.util.List;
import java.util.Map;

/**
 * Interface defines common methods to perform custom queries that are defined in xml files.
 */
public interface QueryExecutor {

    /**
     * Perform query with parameters and return instance with specified type as result.
     *
     * @param queryName  query name
     * @param type       type of object
     * @param <T>        the type of the class modeled by this {@code Class} object.
     * @param parameters query parameters
     * @return object
     */
    <T> T queryForObject(String queryName, Class<T> type, Map<String, Object> parameters);

    /**
     * Perform query without parameters and return instance with specified type as result.
     *
     * @param queryName query name
     * @param type      type of object
     * @param <T>       the type of the class modeled by this {@code Class} object.
     * @return object
     */
    <T> T queryForObject(String queryName, Class<T> type);

    /**
     * Perform query with parameters and return list of objects.
     * Default implementation of list is {@link java.util.ArrayList}.
     *
     * @param queryName  query name
     * @param type       type of object
     * @param parameters query parameters
     * @param <T>        the type of the class modeled by this {@code Class} object.
     * @return list of objects
     */
    <T> List<T> queryForList(String queryName, Class<T> type, Map<String, Object> parameters);

    /**
     * Perform query without parameters and return list of objects.
     * Default implementation of list is {@link java.util.ArrayList}.
     *
     * @param queryName query name
     * @param type      type of object
     * @param <T>       the type of the class modeled by this {@code Class} object.
     * @return list of objects
     */
    <T> List<T> queryForList(String queryName, Class<T> type);
}

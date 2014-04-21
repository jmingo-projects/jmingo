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
package com.mingo;

import com.mingo.convert.ConverterService;
import com.mingo.executor.QueryExecutor;
import com.mingo.mongo.MongoDBFactory;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Interface that specifies a basic set of Mingo operations.
 */
public class MingoTemplate {


    private QueryExecutor queryExecutor;
    private MongoDBFactory mongoDBFactory;
    private ConverterService converterService;

    public MingoTemplate(QueryExecutor queryExecutor, MongoDBFactory mongoDBFactory, ConverterService converterService) {
        this.queryExecutor = queryExecutor;
        this.mongoDBFactory = mongoDBFactory;
        this.converterService = converterService;
    }

    public void dropCollection(String collectionName) {
        mongoDBFactory.getDB().getCollection(collectionName).drop();
    }


    /**
     * Gets list of objects of specified T type from the collection.
     *
     * @param type           the type
     * @param collectionName the collection name
     * @return the list of objects of type
     */
    public <T> List<T> findAll(Class<T> type, String collectionName) {
        List<T> result = new ArrayList<>();
        DBCursor cursor = mongoDBFactory.getDB().getCollection(collectionName).find();
        while (cursor.hasNext()) {
            DBObject object = cursor.next();
            T item = converterService.lookupConverter(type).convert(type, object);
            result.add(item);
        }
        return result;
    }

    /**
     * Perform query with parameters and return instance with specified type as result.
     *
     * @param queryName  query name
     * @param type       type of object
     * @param <T>        the type of the class modeled by this {@code Class} object.
     * @param parameters query parameters
     * @return object
     */
    public <T> T queryForObject(String queryName, Class<T> type, Map<String, Object> parameters) {
        return queryExecutor.queryForObject(queryName, type, parameters);
    }

    /**
     * Perform query without parameters and return instance with specified type as result.
     *
     * @param queryName query name
     * @param type      type of object
     * @param <T>       the type of the class modeled by this {@code Class} object.
     * @return object
     */
    public <T> T queryForObject(String queryName, Class<T> type) {
        return queryExecutor.queryForObject(queryName, type);
    }

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
    public <T> List<T> queryForList(String queryName, Class<T> type, Map<String, Object> parameters) {
        return queryExecutor.queryForList(queryName, type, parameters);
    }

    /**
     * Perform query without parameters and return list of objects.
     * Default implementation of list is {@link java.util.ArrayList}.
     *
     * @param queryName query name
     * @param type      type of object
     * @param <T>       the type of the class modeled by this {@code Class} object.
     * @return list of objects
     */
    public <T> List<T> queryForList(String queryName, Class<T> type) {
        return queryExecutor.queryForList(queryName, type);
    }

}

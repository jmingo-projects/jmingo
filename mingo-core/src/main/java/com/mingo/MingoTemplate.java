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

import com.mingo.benchmark.Profiler;
import com.mingo.benchmark.Profilers;
import com.mingo.mapping.convert.ConverterService;
import com.mingo.executor.QueryExecutor;
import com.mingo.mapping.marshall.BsonMarshaller;
import com.mingo.mapping.marshall.jackson.JacksonBsonMarshallingFactory;
import com.mingo.mongo.MongoDBFactory;
import com.mingo.query.Criteria;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mingo.util.DocumentUtils.assertDocument;
import static com.mingo.util.DocumentUtils.getCollectionName;
import static com.mingo.util.DocumentUtils.getIdValue;

/**
 * Interface that specifies a basic set of Mingo operations.
 */
public class MingoTemplate {


    private QueryExecutor queryExecutor;
    private MongoDBFactory mongoDBFactory;
    private ConverterService converterService;
    private BsonMarshaller bsonMarshaller = JacksonBsonMarshallingFactory.getInstance().createMarshaller();

    public MingoTemplate(QueryExecutor queryExecutor, MongoDBFactory mongoDBFactory, ConverterService converterService) {
        this.queryExecutor = queryExecutor;
        this.mongoDBFactory = mongoDBFactory;
        this.converterService = converterService;
    }

    /**
     * Drops specified collection by name.
     *
     * @param collectionName the collection name
     */
    public void dropCollection(String collectionName) {
        mongoDBFactory.getDB().getCollection(collectionName).drop();
    }

    /**
     * Drops specified collection by name.
     *
     * @param type the type
     */
    public void dropCollection(Class<?> type) {
        mongoDBFactory.getDB().getCollection(getCollectionName(type)).drop();
    }

    /**
     * Inserts the object to the collection for the entity type of the object to save.
     * as the collection name is the {@link com.mingo.annotation.Document#collectionName()} or simple class name
     * of stored object if collection name isn't explicitly defined in @Document annotation.
     *
     * @param objectToInsert the object to store in the collection
     */
    public void insert(Object objectToInsert) {
        assertDocument(objectToInsert);
        String collectionName = getCollectionName(objectToInsert);
        insert(objectToInsert, collectionName);
    }

    public void insert(Object... objectsToInsert) {
        for(Object objectToInsert : objectsToInsert) {
            insert(objectToInsert);
        }
    }

    /**
     * Inserts the object to the collection for the entity type of the object to save.
     *
     * @param objectToInsert the object to store in the collection
     * @param collectionName the collection name
     */
    public void insert(Object objectToInsert, String collectionName) {
        assertDocument(objectToInsert);
        Validate.notBlank(collectionName, "collectionName cannot be null or empty");
        DBObject dbObject = bsonMarshaller.marshall(BasicDBObject.class, objectToInsert);
        mongoDBFactory.getDB().getCollection(collectionName).insert(dbObject);
    }

    public WriteResult update(Object objectToUpdate, Criteria criteria, Class<?> entityClass) {
        assertDocument(objectToUpdate);
        assertDocument(entityClass);
        DBObject updateDocument = bsonMarshaller.marshall(BasicDBObject.class, objectToUpdate);
        DBObject queryObject = criteria.query();
        return update(updateDocument, queryObject, entityClass, criteria.isUpsert(), criteria.isMulti());
    }

    public WriteResult update(DBObject update, DBObject query, Class<?> entityClass, boolean upsert, boolean multi) {
        assertDocument(entityClass);
        String collectionName = getCollectionName(entityClass);
        return mongoDBFactory.getDB().getCollection(collectionName).update(query, update, upsert, multi);
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
     * Gets list of objects of specified T type from the collection.
     *
     * @param type the type
     * @return the list of objects of type
     */
    public <T> List<T> findAll(Class<T> type) {
        String collectionName = getCollectionName(type);
        List<T> result = new ArrayList<>();
        DBCursor cursor = mongoDBFactory.getDB().getCollection(collectionName).find();
        while (cursor.hasNext()) {
            DBObject object = cursor.next();
            T item = converterService.lookupConverter(type).convert(type, object);
            result.add(item);
        }
        return result;
    }

    public <T> T findById(Object id, Class<T> documentType) {
        assertDocument(documentType);
        Criteria criteria = Criteria.whereId(id);
        return findOne(criteria, documentType);
    }

    public <T> T findOne(Criteria criteria, Class<T> documentType) {
        T result = null;
        assertDocument(documentType);
        DBObject query = criteria.query();
        DBCursor cursor = mongoDBFactory.getDB().getCollection(getCollectionName(documentType)).find(query);
        if (cursor.hasNext()) {
            DBObject dbObject = cursor.iterator().next();
            result = converterService.lookupConverter(documentType).convert(documentType, dbObject);
        }
        return result;
    }

    public <T> List<T> find(Criteria criteria, Class<T> documentType) {
        List<T> result = new ArrayList<>();
        assertDocument(documentType);
        DBObject query = criteria.query();
        DBCursor cursor = mongoDBFactory.getDB().getCollection(getCollectionName(documentType)).find(query);
        while (cursor.hasNext()) {
            DBObject object = cursor.next();
            T item = converterService.lookupConverter(documentType).convert(documentType, object);
            result.add(item);
        }
        return result;
    }

    /**
     * Removes the object from the collection by id.
     *
     * @param object the object to remove
     */
    public WriteResult remove(Object object) {
        return remove(object, getCollectionName(object));
    }

    /**
     * Removes the object from the collection.
     *
     * @param object the object to remove
     * @param collection the collection name
     */
    public WriteResult remove(Object object, String collection){
        assertDocument(object);
        Object idValue = getIdValue(object);
        Criteria criteria = Criteria.whereId(idValue);
        DBObject query = criteria.query();
        return remove(query, collection);
    }

    /**
     * Removes all documents from the collection that satisfies a given query criteria.
     *
     * @param query          the query that specifies criteria used to remove documents
     * @param collectionName the collection name
     */
    public WriteResult remove(DBObject query, String collectionName) {
        return mongoDBFactory.getDB().getCollection(collectionName).remove(query);
    }

    /**
     * Removes all documents from the collection that is used to store the instances of documentClass.
     *
     * @param criteria the query criteria
     * @param documentClass the document class
     */
    public <T> void remove(Criteria criteria, Class<T> documentClass){
        WriteResult writeResult = remove(criteria.query(), getCollectionName(documentClass));
        writeResult.getField("");
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
        Profiler profiler = Profilers.newQueryProfiler(queryName).start();
        T result = queryExecutor.queryForObject(queryName, type, parameters);
        profiler.stop();
        return result;
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
        Profiler profiler = Profilers.newQueryProfiler(queryName).start();
        List<T> result = queryExecutor.queryForList(queryName, type, parameters);
        profiler.stop();
        return result;
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

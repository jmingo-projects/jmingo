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
package com.mingo.executor;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mingo.mapping.convert.ConversionUtils;
import com.mingo.mapping.convert.Converter;
import com.mingo.mapping.convert.ConverterService;
import com.mingo.mapping.marshall.JsonToDBObjectMarshaller;
import com.mingo.mapping.marshall.mongo.MongoBsonMarshallingFactory;
import com.mingo.mongo.MongoDBFactory;
import com.mingo.query.QueryManager;
import com.mingo.query.QueryStatement;
import com.mingo.query.QueryType;
import com.mingo.query.el.ELEngine;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mingo.mapping.convert.ConversionUtils.getAsBasicDBList;

/**
 * Implementation of {@link QueryExecutor} that uses MongoDB driver to perform queries.
 */
public class MongoQueryExecutor extends AbstractQueryExecutor implements QueryExecutor {

    private MongoDBFactory mongoDBFactory;

    private QueryManager queryManager;
    private ELEngine elEngine;
    private ConverterService converterService;
    private Map<QueryType, QueryStrategy> queryStrategyMap =
            new ImmutableMap.Builder<QueryType, QueryStrategy>()
                    .put(QueryType.AGGREGATION, new AggregationQueryStrategy())
                    .put(QueryType.PLAIN, new PlainQueryStrategy())
                    .build();

    private static final JsonToDBObjectMarshaller JSON_TO_DB_OBJECT_MARSHALLER = new MongoBsonMarshallingFactory().createJsonToDbObjectMarshaller();

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoQueryExecutor.class);

    /**
     * Constructor to create mongo query executor.
     *
     * @param mongoDBFactory   the mongodb factory
     * @param queryManager     the query manager
     * @param elEngine         the EL engine
     * @param converterService the converter service
     */
    public MongoQueryExecutor(MongoDBFactory mongoDBFactory, QueryManager queryManager, ELEngine elEngine,
                              ConverterService converterService) {
        this.mongoDBFactory = mongoDBFactory;
        this.queryManager = queryManager;
        this.elEngine = elEngine;
        this.converterService = converterService;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T queryForObject(String queryName, Class<T> type, Map<String, Object> parameters) {
        LOGGER.debug("queryForObject(queryName={}, type={}, parameters={})", queryName, type, parameters);
        return doQuery(queryName, type, parameters, QueryStrategy::queryForObject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T queryForObject(String queryName, Class<T> type) {
        return queryForObject(queryName, type, ImmutableMap.<String, Object>of());
    }

    @Override
    public <T> List<T> queryForList(String queryName, Class<T> type, Map<String, Object> parameters) {
        LOGGER.debug("queryForList(queryName={}, type={}, parameters={})", queryName, type, parameters);
        return doQuery(queryName, type, parameters, QueryStrategy::<T>queryForList);
    }

    @Override
    public <T> List<T> queryForList(String queryName, Class<T> type) {
        return queryForList(queryName, type, ImmutableMap.<String, Object>of());
    }

    private <S, R> R doQuery(String queryName, Class<S> type, Map<String, Object> parameters,
                             QueryCallback<S, R> queryCallback) {
        Validate.notEmpty(queryName, "query name cannot be null");
        Validate.notNull(type, "type cannot be null");
        QueryStatement queryStatement = new QueryStatement(queryManager, elEngine, queryName, parameters);
        QueryStrategy queryStrategy = queryStrategyMap.get(queryStatement.getQueryType());
        return queryCallback.query(queryStrategy, queryStatement, type);
    }

    /**
     * Callback pattern.
     *
     * @param <S> the type of target object
     * @param <R> the type of query result
     */
    @FunctionalInterface
    private interface QueryCallback<S, R> {
        R query(QueryStrategy queryStrategy, QueryStatement queryStatement, Class<S> type);
    }

    private DB getDB() {
        return mongoDBFactory.getDB();
    }


    private DBCollection getDbCollection(String collectionName) {
        DB db = getDB();
        return db.getCollection(collectionName);
    }


    private abstract class QueryStrategy {
        abstract <T> List<T> queryForList(QueryStatement queryStatement, Class<T> type);

        abstract <T> T queryForObject(QueryStatement queryStatement, Class<T> type);
    }

    /**
     * Strategy which define behaviour for aggregation mongo query.
     */
    private class AggregationQueryStrategy extends QueryStrategy {

        @Override
        <T> List<T> queryForList(QueryStatement queryStatement, Class<T> type) {
            DBCollection dbCollection = getDbCollection(queryStatement.getCollectionName());
            BasicDBList query = (BasicDBList) JSON_TO_DB_OBJECT_MARSHALLER.marshall(queryStatement.getPreparedQuery(),
                    queryStatement.getParameters());
            AggregationOutput aggregationOutput = performAggregationQuery(dbCollection, query);
            BasicDBList source = getAsBasicDBList(aggregationOutput);
            List<T> result = convertList(type, source, queryStatement.getConverterClass(),
                    queryStatement.getConverterMethod());
            return result != null ? result : Lists.<T>newArrayList();
        }

        @Override
        <T> T queryForObject(QueryStatement queryStatement, Class<T> type) {
            DBCollection dbCollection = getDbCollection(queryStatement.getCollectionName());
            BasicDBList query = (BasicDBList) JSON_TO_DB_OBJECT_MARSHALLER.marshall(queryStatement.getPreparedQuery(),
                    queryStatement.getParameters());
            AggregationOutput aggregationOutput = performAggregationQuery(dbCollection, query);
            BasicDBList result = getAsBasicDBList(aggregationOutput);
            return convertOne(type, result, queryStatement.getConverterClass(), queryStatement.getConverterMethod());
        }
    }

    /**
     * Strategy which define behaviour for simple/plain mongo query.
     */
    private class PlainQueryStrategy extends QueryStrategy {

        @Override
        <T> List<T> queryForList(QueryStatement queryStatement, Class<T> type) {
            DBCollection dbCollection = getDbCollection(queryStatement.getCollectionName());
            DBObject query = JSON_TO_DB_OBJECT_MARSHALLER.marshall(queryStatement.getPreparedQuery(),
                    queryStatement.getParameters());
            DBCursor source = dbCollection.find(query);
            List<T> result = convertList(type, source, queryStatement.getConverterClass(),
                    queryStatement.getConverterMethod());
            return result != null ? result : Lists.<T>newArrayList();
        }

        @Override
        <T> T queryForObject(QueryStatement queryStatement, Class<T> type) {
            DBCollection dbCollection = getDbCollection(queryStatement.getCollectionName());
            DBObject query = JSON_TO_DB_OBJECT_MARSHALLER.marshall(queryStatement.getPreparedQuery(),
                    queryStatement.getParameters());
            DBObject result = dbCollection.findOne(query);
            return convertOne(type, result, queryStatement.getConverterClass(), queryStatement.getConverterMethod());
        }
    }

    private <T> List<T> convertList(Class<T> type, DBCursor result, String converterClass, String converterMethod) {
        List<T> list = null;
        if (result != null && result.hasNext()) {
            list = Lists.newArrayList();
            for (DBObject item : result) {
                list.add(convertOne(type, item, converterClass,
                        converterMethod));
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> convertList(Class<T> type, BasicDBList result, String converterClass, String converterMethod) {
        if (StringUtils.isNotBlank(converterClass) && StringUtils.isNotBlank(converterMethod)) {
            return (List<T>) convertByMethod(result, converterClass, converterMethod);
        } else {
            return convertList(type, result, converterService.lookupConverter(type));
        }
    }

    private <T> T convertOne(Class<T> type, DBObject result, String converterClass, String converterMethod) {
        if (StringUtils.isNotBlank(converterClass) && StringUtils.isNotBlank(converterMethod)) {
            return convertByMethod(result, converterClass, converterMethod);
        } else {
            return converterService.lookupConverter(type).convert(type, result);
        }
    }

    /**
     * Covert result of plain query to an object.
     */
    private <T> T convertOne(Class<T> type, DBObject item, Converter<T> converter) {
        return converter.convert(type, item);
    }

    /**
     * Covert result of aggregation query to list of objects.
     */
    private <T> List<T> convertList(Class<T> type, BasicDBList result,
                                    Converter<T> converter) {
        return ConversionUtils.convertList(type, result, converter);
    }

    /**
     * Covert result of aggregation query to an object.
     */
    @SuppressWarnings("unchecked")
    private <T> T convertByMethod(DBObject source,
                                  String converterClass, String converterMethod) {
        return converterService.convertByMethod(source, converterClass, converterMethod);
    }

}

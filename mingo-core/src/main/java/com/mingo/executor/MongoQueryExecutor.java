package com.mingo.executor;

import static com.mingo.convert.ConversionUtils.getAsBasicDBList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mingo.query.QueryManager;
import com.mingo.convert.ConversionUtils;
import com.mingo.convert.Converter;
import com.mingo.convert.ConverterService;
import com.mingo.mongo.MongoDBFactory;
import com.mingo.query.QueryStatement;
import com.mingo.query.QueryType;
import com.mingo.query.analyzer.QueryAnalyzer;
import com.mingo.query.util.QueryUtils;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Copyright 2012-2013 The Mingo Team
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class MongoQueryExecutor extends AbstractQueryExecutor implements QueryExecutor {

    private MongoDBFactory mongoDBFactory;

    private QueryManager queryManager;
    private QueryAnalyzer queryAnalyzer;
    private ConverterService converterService;
    private Map<QueryType, QueryStrategy> queryStrategyMap =
        new ImmutableMap.Builder<QueryType, QueryStrategy>()
            .put(QueryType.AGGREGATION, new AggregationQueryStrategy())
            .put(QueryType.SIMPLE, new SimpleQueryStrategy())
            .build();

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoQueryExecutor.class);

    public MongoQueryExecutor(MongoDBFactory mongoDBFactory, QueryManager queryManager, QueryAnalyzer queryAnalyzer, ConverterService converterService) {
        this.mongoDBFactory = mongoDBFactory;
        this.queryManager = queryManager;
        this.queryAnalyzer = queryAnalyzer;
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
        QueryStatement queryStatement = new QueryStatement(queryManager, queryAnalyzer, queryName, parameters);
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

    private DB getDB(String queryName) {
        return  mongoDBFactory.getDB();
    }

    private DBCollection getDbCollection(QueryStatement queryStatement) {
        return getDbCollection(queryStatement.getDbName(), queryStatement.getCollectionName());
    }

    private DBCollection getDbCollection(String dbName, String collectionName) {
        DB db = getDB(dbName);
        return db.getCollection(collectionName);
    }

    private DBCollection getDbCollection(String queryName) {
        DB db = getDB(queryName);
        return db.getCollection(QueryUtils.getCollectionName(queryName));
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
            DBCollection dbCollection = getDbCollection(queryStatement);
            AggregationOutput aggregationOutput = performAggregationQuery(dbCollection,
                (BasicDBList) getQueryParser().parse(queryStatement));
            BasicDBList source = getAsBasicDBList(aggregationOutput);
            List<T> result = convertList(type, source, queryStatement.getConverterClass(),
                queryStatement.getConverterMethod());
            return result != null ? result : Lists.<T>newArrayList();
        }

        @Override
        <T> T queryForObject(QueryStatement queryStatement, Class<T> type) {
            DBCollection dbCollection = getDbCollection(queryStatement);
            AggregationOutput aggregationOutput = performAggregationQuery(dbCollection,
                (BasicDBList) getQueryParser().parse(queryStatement));
            BasicDBList result = getAsBasicDBList(aggregationOutput);
            return convertOne(type, result, queryStatement.getConverterClass(), queryStatement.getConverterMethod());
        }
    }

    /**
     * Strategy which define behaviour for simple/plain mongo query.
     */
    private class SimpleQueryStrategy extends QueryStrategy {

        @Override
        <T> List<T> queryForList(QueryStatement queryStatement, Class<T> type) {
            DBCollection dbCollection = getDbCollection(queryStatement);
            DBCursor source = dbCollection.find(getQueryParser().parse(queryStatement));
            List<T> result = convertList(type, source, queryStatement.getConverterClass(),
                queryStatement.getConverterMethod());
            return result != null ? result : Lists.<T>newArrayList();
        }

        @Override
        <T> T queryForObject(QueryStatement queryStatement, Class<T> type) {
            DBCollection dbCollection = getDbCollection(queryStatement);
            DBObject result = dbCollection.findOne(getQueryParser().parse(queryStatement));
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
            Converter<T> converter = converterService.lookupConverter(type);
            if (converter != null) {
                return convertList(type, result, converter);
            }
        }
        return convertList(type, result, converterService.getDefaultConverter());
    }

    private <T> T convertOne(Class<T> type, DBObject result, String converterClass, String converterMethod) {
        if (StringUtils.isNotBlank(converterClass) && StringUtils.isNotBlank(converterMethod)) {
            return convertByMethod(result, converterClass, converterMethod);
        } else {
            Converter<T> converter = converterService.lookupConverter(type);
            if (converter != null) {
                return converter.convert(type, result);
            }
        }
        return convertOne(type, result, converterService.getDefaultConverter());
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

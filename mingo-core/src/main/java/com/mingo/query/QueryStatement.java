package com.mingo.query;

import static com.mingo.query.util.QueryUtils.replaceQueryParameters;
import com.mingo.context.Context;
import com.mingo.query.analyzer.QueryAnalyzer;
import com.mingo.query.util.QueryUtils;
import org.apache.commons.lang3.Validate;

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
public class QueryStatement {

    private String preparedQuery;

    private String dbName;

    private String collectionName;

    private String converterClass;

    private String converterMethod;

    private QueryType queryType = QueryType.SIMPLE;

    /* escape null parameters*/
    private boolean escapeNullParameters;

    /**
     * Gets prepared query.
     *
     * @return prepared query
     */
    public String getPreparedQuery() {
        return preparedQuery;
    }

    /**
     * Gets DB name.
     *
     * @return DB name
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * Gets collection name.
     *
     * @return collection name
     */
    public String getCollectionName() {
        return collectionName;
    }

    /**
     * Gets converter class.
     *
     * @return converter class
     */
    public String getConverterClass() {
        return converterClass;
    }

    /**
     * Gets converterClass method.
     *
     * @return converterClass method
     */
    public String getConverterMethod() {
        return converterMethod;
    }

    /**
     * Gets query type.
     *
     * @return query type
     */
    public QueryType getQueryType() {
        return queryType;
    }

    /**
     * Gets escape null parameters mode.
     *
     * @return true or false
     */
    public boolean isEscapeNullParameters() {
        return escapeNullParameters;
    }

    /**
     * Constructor with parameters.
     *
     * @param context    mingo context
     * @param queryName  query name
     * @param parameters query parameters
     */
    public QueryStatement(Context context, String queryName, Map<String, Object> parameters) {
        Validate.notBlank(queryName, "query name cannot be null or empty");
        Query query = context.lookupQuery(queryName);
        dbName = QueryUtils.getDbName(queryName);
        collectionName = QueryUtils.getCollectionName(queryName);
        prepare(context.getQueryAnalyzer(), query, parameters);
    }

    /**
     * Init process composed from several steps:
     * 1. If there are cases of query then choose which more applicable.
     * if two or more case satisfied then get with higher priority.
     * 2. Replace parameters. parameters in query start with #
     * 3. Get converter class and method.
     *
     * @param queryAnalyzer query analyzer
     * @param pQuery        {@link Query}
     * @param parameters    parameters
     * @return query statement {@link QueryStatement}
     */
    private void prepare(QueryAnalyzer queryAnalyzer, Query pQuery, Map<String, Object> parameters) {
        QueryCase queryCase = queryAnalyzer.analyzeAndGet(pQuery, parameters);
        if (queryCase != null) {
            preparedQuery = replaceQueryParameters(queryCase.getBody(), parameters);
            converterClass = queryCase.getConverter();
            converterMethod = queryCase.getConverterMethod();
            queryType = queryCase.getQueryType();
            escapeNullParameters = pQuery.isEscapeNullParameters();
        } else {
            preparedQuery = replaceQueryParameters(pQuery.getBody(), parameters);
            converterClass = pQuery.getConverter();
            converterMethod = pQuery.getConverterMethod();
            queryType = pQuery.getQueryType();
            escapeNullParameters = pQuery.isEscapeNullParameters();
        }
    }

}

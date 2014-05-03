package com.mingo.query;

import com.mingo.query.el.ELEngine;
import com.mingo.util.QueryUtils;
import org.apache.commons.lang3.Validate;

import java.util.Map;

import static com.mingo.util.QueryUtils.pipeline;

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
public class QueryStatement {

    private String preparedQuery;

    private String collectionName;

    private String converterClass;

    private String converterMethod;

    private QueryType queryType = QueryType.PLAIN;

    /* escape null parameters*/
    private boolean escapeNullParameters;

    private Map<String, Object> parameters;

    /**
     * Gets prepared query.
     *
     * @return prepared query
     */
    public String getPreparedQuery() {
        return preparedQuery;
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

    public Map<String, Object> getParameters() {
        return parameters;
    }

    /**
     * Constructor with parameters.
     *
     * @param queryName  query name
     * @param parameters query parameters
     */
    public QueryStatement(QueryManager queryManager, ELEngine elEngine, String queryName, Map<String, Object> parameters) {
        Validate.notBlank(queryName, "query name cannot be null or empty");
        Query query = queryManager.lookupQuery(queryName);
        collectionName = QueryUtils.getCollectionName(queryName);
        this.parameters = parameters;
        prepare(elEngine, query, parameters);
    }

    /**
     * Build query and prepare for execution.
     *
     * @param elEngine   the EL engine
     * @param pQuery     {@link Query}
     * @param parameters parameters
     */
    private void prepare(ELEngine elEngine, Query pQuery, Map<String, Object> parameters) {
        preparedQuery = pQuery.build(elEngine, parameters);
        converterClass = pQuery.getConverter();
        converterMethod = pQuery.getConverterMethod();
        queryType = pQuery.getQueryType();
        escapeNullParameters = pQuery.isEscapeNullParameters();
    }

}

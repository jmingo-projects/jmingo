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
package com.mingo.query;


import com.mingo.query.el.ELEngine;
import com.mingo.query.el.ELEngineFactory;
import com.mingo.query.el.ELEngineType;
import com.mingo.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.mingo.query.QueryType.PLAIN;
import static com.mingo.util.QueryUtils.pipeline;

/**
 * Implementation of {@link QBuilder} interface that's based on {@link StringBuilder}.
 * Is not thread safe therefore it is necessary to create new instance of builder for each query execution.
 */
public class QueryBuilder implements QBuilder {

    private StringBuilder query = new StringBuilder();
    private ELEngine elEngine = ELEngineFactory.create(ELEngineType.SPRING_EL);
    private Map<String, Object> parameters = Collections.emptyMap();
    private QueryType queryType;

    /**
     * Constructor with parameters.
     *
     * @param queryType  the query type
     * @param parameters the query parameters
     */
    public QueryBuilder(QueryType queryType, Map<String, Object> parameters) {
        this.queryType = queryType;
        this.parameters = parameters;
    }

    /**
     * Constructor with parameters.
     *
     * @param queryType  the query type
     * @param elEngine   the EL engine to evaluate el expressions
     * @param parameters the query parameters
     */
    public QueryBuilder(QueryType queryType, ELEngine elEngine, Map<String, Object> parameters) {
        this.queryType = queryType;
        this.elEngine = elEngine;
        this.parameters = parameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean append(TextElement queryEl) {
        query.append(queryEl.getText());
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean append(ConditionElement conditionEl) {
        boolean appended = false;
        if (elEngine.evaluate(conditionEl.getExpression(), parameters)) {
            query.append(conditionEl.getText());
            appended = true;
        }
        return appended;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean appendIfAbsent(String str) {
        String dump = query.toString();
        StringUtils.appendIfAbsent(query, str);
        return org.apache.commons.lang3.StringUtils.equals(dump, query.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String buildQuery() {
        String prepared = StringUtils.replaceLastComma(query.toString());
        return wrap(queryType, prepared);
    }

    /**
     * Concatenates query elements in one single string.
     * {@link QueryElement#asString()} method will be invoked for each element from queryElements.
     *
     * @param qType         the type of query
     * @param queryElements the query elements to collect in single string
     * @return query text
     */
    public static String getFullQueryText(QueryType qType, List<QueryElement> queryElements) {
        StringBuilder builder = new StringBuilder();
        queryElements.forEach(el -> {
            String elText = el.asString();
            builder.append(StringUtils.appendIfAbsent(elText, ","));
        });
        String text = StringUtils.replaceLastComma(builder.toString());
        return wrap(qType, text);
    }

    private static String wrap(QueryType qType, String str) {
        if (PLAIN.equals(qType)) {
            StringBuilder builder = new StringBuilder();
            str = org.apache.commons.lang3.StringUtils.trim(str);
            if (!str.startsWith("{")) {
                builder.append("{");
            }
            builder.append(str);
            if (!str.endsWith("}")) {
                builder.append("}");
            }
            return builder.toString();
        }
        return pipeline(str);
    }

}

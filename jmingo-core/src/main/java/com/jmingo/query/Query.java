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
package com.jmingo.query;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.jmingo.query.el.ELEngine;

import java.util.List;
import java.util.Map;

import com.jmingo.util.QueryUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;

import static com.jmingo.query.QueryType.PLAIN;

/**
 * Mingo query. Immutable.
 */
public class Query {

    private final String id;

    private final String compositeId;

    private final String converterClass;

    private final String converterMethod;

    /* escape null parameters*/
    private final boolean escapeNullParameters;

    private final QueryType queryType;

    private final List<QueryElement> queryElements;

    /**
     * Creates builder.
     *
     * @return builder
     */
    public static Builder builder() {
        return new Builder();
    }

    private Query(Builder builder) {
        this.id = builder.id;
        this.compositeId = builder.compositeId();
        this.converterClass = builder.converterClass;
        this.converterMethod = builder.converterMethod;
        this.escapeNullParameters = builder.escapeNullParameters;
        this.queryType = builder.queryType;
        this.queryElements = ImmutableList.copyOf(builder.queryElements);
    }

    /**
     * Gets id.
     *
     * @return query id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets query text.
     *
     * @return query text
     */
    public String getText() {
        return QueryBuilder.getFullQueryText(queryType, queryElements);
    }

    /**
     * Gets composite ID.
     *
     * @return composite ID
     */
    public String getCompositeId() {
        return compositeId;
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
     * Gets converter class.
     *
     * @return converter class
     */
    public String getConverterClass() {
        return converterClass;
    }

    /**
     * Gets converter method.
     *
     * @return converter method
     */
    public String getConverterMethod() {
        return converterMethod;
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
     * Gets query elements.
     *
     * @return query elements
     */
    public List<QueryElement> getQueryElements() {
        return queryElements;
    }

    /**
     * Creates string representation of query.
     *
     * @param elEngine   the EL engine
     * @param parameters the query parameters
     * @return string representation of query
     */
    public String build(ELEngine elEngine, Map<String, Object> parameters) {
        Validate.notNull(elEngine, "el engine cannot be null");
        QBuilder queryBuilder = new QueryBuilder(queryType, elEngine, parameters);
        queryElements.forEach(element -> element.accept(queryBuilder));
        return queryBuilder.buildQuery();
    }

    /**
     * Builder for {@link com.jmingo.query.Query}.
     */
    public static class Builder {

        private String id;
        private String collectionName;
        private String converterClass;
        private String converterMethod;
        /* escape null parameters*/
        private boolean escapeNullParameters = false;
        private QueryType queryType = PLAIN;
        private List<QueryElement> queryElements = Lists.newArrayList();

        public Builder id(String val) {
            Validate.notBlank(val, "query id is required field and cannot be null");
            this.id = val;
            return this;
        }

        public Builder collectionName(String val) {
            Validate.notBlank(val, "collection name is required field and cannot be null");
            this.collectionName = val;
            return this;
        }

        public Builder converterClass(String val) {
            this.converterClass = val;
            return this;
        }

        public Builder converterMethod(String val) {
            this.converterMethod = val;
            return this;
        }

        public Builder escapeNullParameters(boolean val) {
            this.escapeNullParameters = val;
            return this;
        }

        public Builder queryType(QueryType queryType) {
            Validate.notNull(queryType, "query type is required field and cannot be null");
            this.queryType = queryType;
            return this;
        }

        public Builder add(QueryElement queryEl) {
            queryElements.add(queryEl);
            return this;
        }

        public Builder add(List<QueryElement> qElements) {
            if (CollectionUtils.isNotEmpty(qElements)) {
                queryElements.addAll(qElements);
            }
            return this;
        }

        public Builder addTextElement(String text) {
            queryElements.add(new TextElement(text));
            return this;
        }

        public String compositeId() {
            return QueryUtils.buildCompositeId(collectionName, id);
        }

        public Query build() {
            return new Query(this);
        }
    }

}

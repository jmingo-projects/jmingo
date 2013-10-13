package com.mingo.query;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
public class QueryCase extends QuerySetElement implements Comparable<QueryCase> {

    private int priority;

    /* condition define in EL format*/
    private String condition;

    private String converter;

    private String converterMethod;

    private QueryType queryType = QueryType.SIMPLE;

    /**
     * Default constructor.
     */
    public QueryCase() {
    }

    /**
     * Constructor with parameters.
     *
     * @param id id
     */
    public QueryCase(String id) {
        super(id);
    }

    /**
     * Constructor with parameters.
     *
     * @param id   id
     * @param body body
     */
    public QueryCase(String id, String body) {
        super(id, body);
    }

    /**
     * Gets priority.
     *
     * @return priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Sets priority.
     *
     * @param priority priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Gets condition.
     *
     * @return condition
     */
    public String getCondition() {
        return condition;
    }

    /**
     * Sets condition.
     *
     * @param condition condition
     */
    public void setCondition(String condition) {
        this.condition = condition;
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
     * Sets converter method.
     *
     * @param converterMethod converter method
     */
    public void setConverterMethod(String converterMethod) {
        this.converterMethod = converterMethod;
    }

    /**
     * Gets converter.
     *
     * @return converter
     */
    public String getConverter() {
        return converter;
    }

    /**
     * Sets converter.
     *
     * @param converter converter
     */
    public void setConverter(String converter) {
        this.converter = converter;
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
     * Sets queryType.
     *
     * @param queryType query type
     */
    public void setQueryType(QueryType queryType) {
        if (queryType != null) {
            this.queryType = queryType;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QueryCase)) {
            return false;
        }

        QueryCase that = (QueryCase) o;
        return new EqualsBuilder()
            .appendSuper(super.equals(o))
            .append(converter, that.converter)
            .append(converterMethod, that.converterMethod)
            .append(priority, that.priority)
            .append(queryType, that.queryType)
            .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .appendSuper(super.hashCode())
            .append(converter)
            .append(converterMethod)
            .append(priority)
            .append(queryType)
            .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).
            append("id", getId()).
            append("body", getBody()).
            append("priority", priority).
            append("condition", condition).
            append("converter", converter).
            append("converterMethod", converterMethod).
            append("queryType", queryType).
            toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(QueryCase queryCase) {
        Validate.notNull(queryCase, "query case cannot be null.");
        return Integer.compare(queryCase.getPriority(), this.priority);
    }

}

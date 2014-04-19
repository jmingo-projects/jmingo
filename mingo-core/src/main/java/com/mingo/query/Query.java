package com.mingo.query;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Collections;
import java.util.Set;

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
public class Query extends QuerySetElement {

    private String compositeId;

    private String converter;

    private String converterMethod;

    /* escape null parameters*/
    private boolean escapeNullParameters = true;

    private QueryType queryType = QueryType.SIMPLE;

    private Set<QueryCase> cases = Collections.emptySet();

    /**
     * Default constructor.
     */
    public Query() {
    }

    /**
     * Constructor with parameters.
     *
     * @param id id
     */
    public Query(String id) {
        super(id);
    }

    /**
     * Constructor with parameters.
     *
     * @param id   id
     * @param body body
     */
    public Query(String id, String body) {
        super(id, body);
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
     * Set composite id.
     *
     * @param compositeId composite id
     */
    public void setCompositeId(String compositeId) {
        this.compositeId = compositeId;
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
        if (StringUtils.isNotBlank(converter)) {
            this.converter = converter;
        }

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
        if (StringUtils.isNotBlank(converterMethod)) {
            this.converterMethod = converterMethod;
        }
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
     * Gets cases.
     *
     * @return cases
     */
    public Set<QueryCase> getCases() {
        return ImmutableSortedSet.copyOf(cases);
    }

    /**
     * Add query case.
     *
     * @param queryCase query case
     */
    public void addQueryCase(QueryCase queryCase) {
        Validate.notNull(queryCase, "query case cannot be null");
        if (CollectionUtils.isEmpty(cases)) {
            cases = Sets.newTreeSet();
        }
        cases.add(queryCase);
    }

    /**
     * Gets query case by id.
     *
     * @param caseId case id
     * @return query case
     */
    public QueryCase getQueryCaseById(final String caseId) {
        return Iterables.find(cases, input -> input.getId().equals(caseId));
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
     * Sets escape null parameters mode.
     *
     * @param escapeNullParameters true or false
     */
    public void setEscapeNullParameters(boolean escapeNullParameters) {
        this.escapeNullParameters = escapeNullParameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Query)) {
            return false;
        }

        Query that = (Query) o;
        return new EqualsBuilder()
            .appendSuper(super.equals(o))
            .append(compositeId, that.compositeId)
            .append(converter, that.converter)
            .append(converterMethod, that.converterMethod)
            .append(escapeNullParameters, that.escapeNullParameters)
            .append(queryType, that.queryType)
            .append(cases, that.cases)
            .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .appendSuper(super.hashCode())
            .append(compositeId)
            .append(converter)
            .append(converterMethod)
            .append(escapeNullParameters)
            .append(queryType)
            .append(cases)
            .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).
            append("id", getId()).
            append("body", getBody()).
            append("compositeId", compositeId).
            append("converter", converter).
            append("converterMethod", converterMethod).
            append("escapeNullParameters", escapeNullParameters).
            append("queryType", queryType).
            append("cases", cases).
            toString();
    }

}

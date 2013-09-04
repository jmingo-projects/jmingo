package com.mingo.query;

import org.apache.commons.lang3.Validate;

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
public class QueryCase implements Comparable<QueryCase> {

    private String id;

    private int priority;

    /* condition define in EL format*/
    private String condition;

    /* contains query which satisfies condition */
    private String body;

    private String converter;

    private String converterMethod;

    private QueryType queryType = QueryType.SIMPLE;

    /**
     * Gets id.
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id id
     */
    public void setId(String id) {
        this.id = id;
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
     * Gets body.
     *
     * @return body
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets body.
     *
     * @param body body
     */
    public void setBody(String body) {
        this.body = body;
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
    public String toString() {
        return "QueryCase{" +
            "id='" + id + '\'' +
            ", queryType=" + queryType +
            ", priority=" + priority +
            ", condition='" + condition + '\'' +
            ", body='" + body + '\'' +
            ", converter='" + converter + '\'' +
            '}';
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

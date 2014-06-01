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


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * This class is used to configure a request for mongodb.
 */
public class Criteria {

    private String queryTemplate;
    private boolean multi;
    private boolean upsert;
    private Map<String, Object> parameters = Maps.newHashMap();

    private static final Criteria EMPTY_CRITERIA = new Criteria("{}");

    /**
     * Create criteria with query template.
     *
     * @param queryTemplate the query template
     */
    public Criteria(String queryTemplate) {
        this.queryTemplate = queryTemplate;
    }

    /**
     * Create criteria with query template.
     *
     * @param queryTemplate the query template
     * @return criteria
     */
    public static Criteria where(String queryTemplate) {
        return new Criteria(queryTemplate);
    }

    /**
     * Create predefined criteria.
     *
     * @param id the id to create criteria
     * @return criteria
     */
    public static Criteria whereId(Object id) {
        return Criteria.where("{'_id' : '#_id'}").with("_id", id);
    }

    /**
     * Gets empty criteria.
     *
     * @return empty criteria
     */
    public static Criteria empty() {
        return EMPTY_CRITERIA;
    }

    /**
     * Adds parameter.
     *
     * @param paramName  the parameter name
     * @param paramValue the parameter value
     * @return current criteria
     */
    public Criteria with(String paramName, Object paramValue) {
        parameters.put(paramName, paramValue);
        return this;
    }

    /**
     * Sets 'multi' field with true value. Specifies that an operation should be applied for all documents
     * that satisfy current criteria.
     *
     * @return current criteria
     */
    public Criteria updateMulti() {
        multi = true;
        return this;
    }

    /**
     * Sets 'multi' field with false value. Specifies that an operation should be applied for the first document
     * that satisfy current criteria.
     *
     * @return current criteria
     */
    public Criteria updateFirst() {
        multi = false;
        return this;
    }

    /**
     * Set upsert field with value 'u'
     *
     * @param u the value to set
     * @return current criteria
     */
    private Criteria upsert(boolean u) {
        upsert = u;
        return this;
    }

    /**
     * Is multi.
     *
     * @return is multi
     */
    public boolean isMulti() {
        return multi;
    }

    /**
     * Is upsert.
     *
     * @return is upsert
     */
    public boolean isUpsert() {
        return upsert;
    }

    /**
     * Gets query.
     * @return query
     */
    public String query() {
        return queryTemplate;
    }

    /**
     * Gets parameters.
     *
     * @return parameters
     */
    public Map<String, Object> getParameters() {
        return ImmutableMap.copyOf(parameters);
    }
}

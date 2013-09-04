package com.mingo.query;

import static com.mingo.query.util.QueryUtils.getQueryId;
import static com.mingo.query.util.QueryUtils.validateCompositeId;
import static org.slf4j.helpers.MessageFormatter.format;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.Validate;

import java.util.Collections;
import java.util.Map;
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
public class QuerySet {

    private String path;

    private String dbName;

    private String collectionName;

    private Map<String, Query> queries = Collections.emptyMap();

    private Set<QueryFragment> queryFragments = Collections.emptySet();

    private static final String DUPLICATED_QUERY_ID = "duplicated query id: '{}'";

    /**
     * Gets path.
     *
     * @return path
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets path.
     *
     * @param path path
     */
    public void setPath(String path) {
        this.path = path;
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
     * Sets DB name.
     *
     * @param dbName DB name
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * Gets common collection name.
     *
     * @return common collection name
     */
    public String getCollectionName() {
        return collectionName;
    }

    /**
     * Sets common collection name.
     *
     * @param collectionName common collection name
     */
    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    /**
     * Gets queries.
     *
     * @return queries
     */
    public Map<String, Query> getQueries() {
        return ImmutableMap.copyOf(queries);
    }

    /**
     * Add query.
     *
     * @param query query
     */
    public void addQuery(Query query) {
        Validate.notNull(query, "query parameter cannot be null");

        if (MapUtils.isEmpty(queries)) {
            queries = Maps.newHashMap();
        }
        if (queries.containsKey(query.getId())) {
            throw new IllegalArgumentException(
                format(DUPLICATED_QUERY_ID, query.getId()).getMessage());
        }
        queries.put(query.getId(), query);
    }

    /**
     * Add query.
     *
     * @param pQueries set of {@link Query} objects
     */
    public void addQuery(Set<Query> pQueries) {
        if (CollectionUtils.isNotEmpty(pQueries)) {
            for (Query query : pQueries) {
                addQuery(query);
            }
        }
    }

    /**
     * Gets query by id.
     *
     * @param id id
     * @return {@link Query}
     */
    public Query getQueryById(String id) {
        Query query = null;
        if (MapUtils.isNotEmpty(queries)) {
            query = queries.get(id);
        }
        return query;
    }

    /**
     * Checks if query exist.
     *
     * @param id id
     * @return true - if query exist, otherwise - false
     */
    public boolean queryExist(String id) {
        return getQueryById(id) != null ? true : false;
    }

    /**
     * Gets query by composite id.
     *
     * @param compositeId composite id
     * @return {@link Query}
     */
    public Query getQueryByCompositeId(String compositeId) {
        validateCompositeId(compositeId);
        Query query = null;
        String queryId = getQueryId(compositeId);
        if (MapUtils.isNotEmpty(queries) && queries.containsKey(queryId)) {
            query = queries.get(queryId);
            // composite Ids must be equivalent
            query = query.getCompositeId().toLowerCase().contains(compositeId.toLowerCase()) ? query : null;
        }
        return query;
    }

    /**
     * Add query fragment.
     *
     * @param queryFragment query fragment
     */
    public void addQueryFragment(QueryFragment queryFragment) {
        Validate.notNull(queryFragment, "query fragment cannot be empty");
        if (CollectionUtils.isEmpty(queryFragments)) {
            queryFragments = Sets.newHashSet();
        }
        queryFragments.add(queryFragment);
    }

    /**
     * Gets query fragments.
     *
     * @return query fragments
     */
    public Set<QueryFragment> getQueryFragments() {
        return queryFragments;
    }

    /**
     * Sets query fragments.
     *
     * @param queryFragments query fragments
     */
    public void setQueryFragments(Set<QueryFragment> queryFragments) {
        this.queryFragments = queryFragments;
    }

    /**
     * Gets query fragment by id.
     *
     * @param id fragment id
     * @return fragment
     */
    public QueryFragment getQueryFragmentById(String id) {
        if (CollectionUtils.isNotEmpty(queryFragments)) {
            for (QueryFragment qf : queryFragments) {
                if (qf.getId().equals(id)) {
                    return qf;
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "QuerySet{" +
            "dbName='" + dbName + '\'' +
            ", collectionName='" + collectionName + '\'' +
            ", queries=" + queries +
            '}';
    }

}

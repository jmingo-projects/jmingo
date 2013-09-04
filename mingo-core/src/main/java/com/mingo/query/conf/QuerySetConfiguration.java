package com.mingo.query.conf;

import com.google.common.collect.Sets;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;

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
public class QuerySetConfiguration {

    private String databaseName;

    private Set<String> querySets = Collections.emptySet();

    /**
     * Gets database name.
     *
     * @return database name
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * Sets database name.
     *
     * @param databaseName database name
     */
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * Gets query sets.
     *
     * @return query sets
     */
    public Set<String> getQuerySets() {
        return querySets;
    }

    /**
     * Add query set.
     *
     * @param querySet query set
     */
    public void addQuerySet(String querySet) {
        Validate.notBlank(querySet, "query set path cannot be empty");
        if (CollectionUtils.isEmpty(querySets)) {
            querySets = Sets.newHashSet();
        }
        querySets.add(querySet);
    }

    /**
     * Add query sets.
     *
     * @param pQuerySets query sets
     */
    public void addQuerySet(Set<String> pQuerySets) {
        if (CollectionUtils.isNotEmpty(pQuerySets)) {
            if (CollectionUtils.isEmpty(querySets)) {
                querySets = Sets.newHashSet(pQuerySets);
            }
            querySets.addAll(pQuerySets);
        }
    }
}

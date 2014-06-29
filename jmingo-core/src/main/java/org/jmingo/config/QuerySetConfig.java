/**
 * Copyright 2013-2014 The JMingo Team
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
package org.jmingo.config;

import com.google.common.collect.Sets;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;

import java.util.Set;

/**
 * Contains properties related to the query sets.
 */
public class QuerySetConfig {

    private Set<String> querySets = Sets.newHashSet();

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
        querySets.add(querySet);
    }

    /**
     * Add query sets.
     *
     * @param pQuerySets the query sets to add
     */
    public void addQuerySet(Set<String> pQuerySets) {
        if (CollectionUtils.isNotEmpty(pQuerySets)) {
            querySets.addAll(pQuerySets);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("QuerySetConfig{");
        sb.append("querySets=").append(querySets);
        sb.append('}');
        return sb.toString();
    }
}

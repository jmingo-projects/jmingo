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

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Represents query types.
 */
public enum QueryType {

    /**
     * Aggregation MongoDB query.
     */
    AGGREGATION("aggregation"),

    /**
     * Plain MongoDB query.
     */
    PLAIN("plain");

    private String name;

    /**
     * Gets name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    private QueryType(String name) {
        this.name = name;
    }

    /**
     * Gets enum by name.
     *
     * @param name name
     * @return {@link QueryType}
     */
    public static QueryType getByName(final String name) {
        return Iterables.find(Lists.newArrayList(values()),
                input -> input.getName().equals(name), null);
    }
}

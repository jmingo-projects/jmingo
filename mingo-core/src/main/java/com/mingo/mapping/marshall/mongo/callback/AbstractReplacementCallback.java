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
package com.mingo.mapping.marshall.mongo.callback;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Map;

/**
 * Base implementation of {@link ReplacementCallback}.
 *
 * @param <T> type of replaced object
 */
public abstract class AbstractReplacementCallback<T> implements ReplacementCallback<T> {

    private String prefix = DEFAULT_PREFIX;
    private Map<String, Object> replacements;

    private static final String DEFAULT_PREFIX = "#";

    /**
     * Constructor this parameters.
     *
     * @param prefix       the prefix identifies that field is parameter
     * @param replacements the replacements
     */
    public AbstractReplacementCallback(String prefix, Map<String, Object> replacements) {
        this.prefix = prefix;
        this.replacements = replacements;
    }

    /**
     * Constructor this parameters.
     *
     * @param replacements the replacements
     */
    public AbstractReplacementCallback(Map<String, Object> replacements) {
        this(DEFAULT_PREFIX, replacements);
    }

    /**
     * Gets parameter prefix.
     *
     * @return parameter prefix
     */
    String getPrefix() {
        return prefix;
    }

    /**
     * Checks that the given value is a collection.
     *
     * @param value the value to check
     * @return true if value is instance of Collection, otherwise - false
     */
    boolean isCollection(Object value) {
        return (value instanceof Collection);
    }

    /**
     * Checks that the given value is a map.
     *
     * @param value the value to check
     * @return true if value is instance of Map, otherwise - false
     */
    boolean isMap(Object value) {
        return (value instanceof Map);
    }

    /**
     * Applies {@link #getReplacement(Object)} for each element in the collection.
     *
     * @param collection the collection to replace
     * @return new collection with replaced elements
     */
    Object replaceInCollection(Collection collection) {
        return Lists.newArrayList(Iterables.transform(collection, this::getReplacement));
    }

    /**
     * Gets a replacement for the given source.
     *
     * @param source the source to get replacement
     * @return replacement
     */
    Object getReplacement(Object source) {
        if (source == null) {
            return source;
        }
        String key = source.toString().replaceFirst(getPrefix(), "");
        return replacements.containsKey(key) ? replacements.get(key) : source;
    }

}

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

import java.util.Collection;
import java.util.Map;


public abstract class AbstractReplacementCallback<T> implements ReplacementCallback<T> {

    private String prefix = DEFAULT_PREFIX;
    private Map<String, Object> replacements;

    private static final String DEFAULT_PREFIX = "#";

    public AbstractReplacementCallback(String prefix, Map<String, Object> replacements) {
        this.prefix = prefix;
        this.replacements = replacements;
    }

    public AbstractReplacementCallback(Map<String, Object> replacements) {
        this(DEFAULT_PREFIX, replacements);
    }

    String getPrefix() {
        return prefix;
    }

    boolean isCollection(Object val) {
        return val instanceof Collection;
    }

    boolean isMap(Object value) {
        return (value instanceof Map);
    }

    Object replaceInCollection(Collection collection) {
        return Iterables.transform(collection, this::getReplacement);
    }

    Object getReplacement(Object source) {
        if (source == null) {
            return source;
        }
        String key = source.toString().replaceFirst(getPrefix(), "");
        return replacements.containsKey(key) ? replacements.get(key) : source;
    }

}

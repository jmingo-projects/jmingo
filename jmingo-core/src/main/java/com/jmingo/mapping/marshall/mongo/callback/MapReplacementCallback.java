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
package com.jmingo.mapping.marshall.mongo.callback;

import com.jmingo.util.MapTraversal;

import java.util.Collection;
import java.util.Map;

/**
 * Callback to replace values in map.
 */
public class MapReplacementCallback extends AbstractReplacementCallback<Map> implements ReplacementCallback<Map> {

    /**
     * Constructor this parameters.
     *
     * @param prefix       the prefix identifies that field is parameter
     * @param replacements the replacements
     */
    public MapReplacementCallback(String prefix, Map<String, Object> replacements) {
        super(prefix, replacements);
    }

    /**
     * Constructor this parameters.
     *
     * @param replacements the replacements
     */
    public MapReplacementCallback(Map<String, Object> replacements) {
        super(replacements);
    }

    /**
     * Replaces all values in map with replacements.
     *
     * @param map the map to replace
     * @return map with replaced values
     */
    @Override
    public Object doReplace(Map map) {
        MapTraversal.traverseEntries(map, (currPap, entry) -> {
            Object value = entry.getValue();
            Object key = entry.getKey();
            Object newValue = isCollection(value) ? replaceInCollection((Collection) value) :
                    getReplacement(value);
            currPap.replace(key, value, newValue);
        }, o -> !isMap(o));
        return map;
    }

}

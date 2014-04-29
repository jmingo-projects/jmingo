/**
 * Copyright 2012-2013 The Mingo Team
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
package com.mingo.util;

import java.util.Map;
import java.util.function.Predicate;


public class MapTraversal {

    public static <K, V> void traverseEntries(Map<K, V> map, MapEntryTraversal traversal, Predicate<V> filter) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            V value = entry.getValue();
            if (filter.test(value)) {
                traversal.accept(map, entry);
            }
            if (isMap(value)) {
                traverseEntries(to(value), traversal, filter);
            }
        }
    }

    private static boolean isMap(Object value) {
        return value instanceof Map;
    }

    @SuppressWarnings("unchecked")
    private static <T> T to(Object o) {
        return (T) o;
    }

    public interface MapEntryTraversal<K, V> {
        public void accept(Map<K, V> map, Map.Entry<K, V> entry);
    }
}

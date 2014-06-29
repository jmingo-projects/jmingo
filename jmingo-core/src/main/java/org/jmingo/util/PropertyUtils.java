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
package org.jmingo.util;


import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.function.Function;

public final class PropertyUtils {
    private PropertyUtils() {
        throw new UnsupportedOperationException("not allowed to create instances of this class");
    }

    private static final Map<Class<?>, Function<String, Object>> VALUE_TRANSFORMER =
            ImmutableMap.<Class<?>, Function<String, Object>>builder()
                    .put(Short.class, Short::parseShort)
                    .put(Integer.class, Integer::parseInt)
                    .put(Long.class, Long::parseLong)
                    .put(Float.class, Float::parseFloat)
                    .put(Double.class, Double::parseDouble)
                    .put(Boolean.class, Boolean::parseBoolean)
                    .put(String.class, String::toString)
                            // primitive types
                    .put(Short.TYPE, Short::parseShort)
                    .put(Integer.TYPE, Integer::parseInt)
                    .put(Long.TYPE, Long::parseLong)
                    .put(Float.TYPE, Float::parseFloat)
                    .put(Double.TYPE, Double::parseDouble)
                    .put(Boolean.TYPE, Boolean::parseBoolean)
                    .build();

    /**
     * Transforms the value from string in value of the given type.
     *
     * @param type  the type to which the given value should be transformed
     * @param value the value to transform
     * @param <T>   the type of the class modeled by this {@code Class} object.
     * @return the transformed value with the given type
     * @throws {@link IllegalArgumentException} if no transformer for the given type
     */
    @SuppressWarnings("unchecked")
    public static <T> T transform(Class<T> type, String value) throws IllegalArgumentException {
        if (!VALUE_TRANSFORMER.containsKey(type)) {
            throw new IllegalArgumentException("unsupported parameter type: " + type);
        }
        // since VALUE_TRANSFORMER contains the transformer for the given type
        // so we can make cast without thinking about ClassCastException
        return (T) VALUE_TRANSFORMER.get(type).apply(value);
    }

    public static void main(String[] args) {
        Integer integer = transform(Integer.class, "5");
        System.out.println(Integer.class.isAssignableFrom(integer.getClass()));
    }

}

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


import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectionUtils {

    public static List<Field> getFields(Class<?> type) {
        return Lists.newArrayList(type.getDeclaredFields());
    }

    /**
     * Gets a method by name ignoring case. Be careful, this method returns first matched method.
     *
     * @param type the class to be searched
     * @param name the method name
     * @return the first first method from {@code type} that satisfies the given name or null if no method
     * in {@code type} matches the given name
     */
    public static Method getMethodByName(Class<?> type, String name) {
        return Iterables.tryFind(Arrays.asList(type.getDeclaredMethods()), (Method method) ->
                org.apache.commons.lang3.StringUtils.equalsIgnoreCase(name, method.getName())).orNull();
    }

    /**
     * Finds a method that satisfies the given predicate.
     *
     * @param type      the class to be searched
     * @param predicate the predicate
     * @return the first first method from {@code type} that satisfies the given name or null if no method
     * in {@code type} matches the given name
     */
    public static Method findMethod(Class<?> type, Predicate<Method> predicate) {
        return Iterables.tryFind(Arrays.asList(type.getDeclaredMethods()), predicate).orNull();
    }

    public static List<Field> getInheritedFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }

    public static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = getFields(type);
        fields.addAll(getInheritedFields(type));
        return fields;
    }

}

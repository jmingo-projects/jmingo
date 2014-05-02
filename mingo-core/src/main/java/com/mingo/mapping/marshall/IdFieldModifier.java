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
package com.mingo.mapping.marshall;


import com.google.common.base.Throwables;
import com.mingo.annotation.AutoGenerate;
import com.mingo.annotation.Document;
import com.mingo.annotation.Id;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Predicate;


/**
 * Modifies fields that is annotated with @Id.
 */
public class IdFieldModifier {

    /**
     * Generates ids values for all pojo's fields including
     * inherited fields that annotated with {@link com.mingo.annotation.Id} and with {@link com.mingo.annotation.AutoGenerate}.
     * <p>
     * If {@code pojo} isn't annotated with {@link com.mingo.annotation.Document} then it will be skipped.
     * Generation depends on type of field:
     * to generate value for {@code String} field the {@link java.util.UUID} is used.
     * If type of id field is {@code ObjectId} then the new instance of {@code ObjectId}
     * will be created using default constructor {@code new ObjectId()}.
     * <p>
     * Generates values for nested fields that also have an id field, for instance if the pojo has collection of mingo
     * documents then for each document form collection will be generated new value.
     * It applies for {@code Collection} and {@code Array} types,
     * exception case for {@code Map}.
     * <p>
     * If an id field is final, Synthetic or static then new value will not be generated.
     *
     * @param pojo the pojo for that the id field should be generated
     */
    public void generateId(Object pojo) {
        if (!isDocument(pojo)) {
            return;
        }

        doWithFields(pojo, pojo.getClass(), generateId, skipSyntheticField,
                skipFinalField, skipStaticField);
    }

    private boolean isDocument(Object pojo) {
        return pojo.getClass().isAnnotationPresent(Document.class);
    }

    private boolean isId(Field field) {
        return field.isAnnotationPresent(Id.class);
    }

    private void generateIdForArray(Object[] objects) {
        for (Object object : objects) {
            generateId(object);
        }
    }

    private void generateIdForList(Collection<?> objects) {
        objects.forEach(this::generateId);
    }

    private void checkAndGenerateId(Object pojo, Field field) throws IllegalAccessException {
        Object val = field.get(pojo);
        if (val == null && field.isAnnotationPresent(AutoGenerate.class)) {

            if (String.class.isAssignableFrom(field.getType())) {
                generateAndSetUUIDId(pojo, field);

            } else if (ObjectId.class.isAssignableFrom(field.getType())) {
                generateAndSetNativeId(pojo, field);
            }
        }
    }

    private void generateAndSetNativeId(Object pojo, Field field) {
        setValue(pojo, field, new ObjectId());
    }

    private void generateAndSetUUIDId(Object pojo, Field field) {
        setValue(pojo, field, UUID.randomUUID().toString());
    }

    private void setValue(Object pojo, Field field, Object val) {
        try {
            field.set(pojo, val);
        } catch (IllegalAccessException e) {
            throw Throwables.propagate(e);
        }
    }


    private FieldCallback generateId = (target, field) -> {
        try {
            field.setAccessible(true); //make field accessible to set/get value
            if (isId(field)) {
                checkAndGenerateId(target, field);
            } else {
                Object child = field.get(target);
                if (child != null) {
                    if (child instanceof Collection) {
                        generateIdForList((Collection) child);
                    } else if (child instanceof Object[]) {
                        generateIdForArray((Object[]) child);
                    } else {
                        generateId(child);
                    }
                }
            }

        } catch (Throwable e) {
            throw Throwables.propagate(e);
        }
    };

    private interface FieldCallback {
        void apply(Object target, Field field);
    }

    /**
     * Filter for fields.
     */
    private Predicate<Field> skipSyntheticField = field -> !field.isSynthetic();
    private Predicate<Field> skipFinalField = field -> !Modifier.isFinal(field.getModifiers());
    private Predicate<Field> skipStaticField = field -> !Modifier.isStatic(field.getModifiers());

    private void doWithFields(Object target, Class<?> clazz, FieldCallback fc, Predicate<Field>... filters)
            throws IllegalArgumentException {
        Class<?> targetClass = clazz;
        do {
            Field[] fields = targetClass.getDeclaredFields();
            for (Field field : fields) {
                // Skip static and final fields.
                if (filters != null && filters.length > 0 && !applyFilters(field, filters)) {
                    continue;
                }
                fc.apply(target, field);
            }
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != null && targetClass != Object.class);
    }

    private boolean applyFilters(Field field, Predicate<Field>... filters) {
        for (Predicate<Field> filter : Arrays.asList(filters)) {
            if (!filter.test(field)) {
                return false;
            }
        }
        return true;
    }

}

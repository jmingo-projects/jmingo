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
package com.mingo.marshall;


import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.mingo.annotation.AutoGenerate;
import com.mingo.annotation.Document;
import com.mingo.annotation.Id;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;


/**
 * Modifies fields that is annotated with @Id.
 */
public class IdFieldModifier {

    /**
     * Generates id value for specified pojo including all pojo's fields if it has field that annotated with @Id and
     * pojo is annotated with @Document annotation.
     *
     * @param pojo the pojo for this the id field should be generated
     */
    public void generateId(Object pojo) {
        if (!isDocument(pojo)) {
            return;
        }
        validate(pojo);

        fields(pojo).forEach(field -> {
            try {
                field.setAccessible(true);
                if (isId(field)) {
                    checkAndGenerateId(pojo, field);
                } else {
                    Object child = field.get(pojo);
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
        });
    }

    private List<Field> fields(Object pojo) {
        return Arrays.asList(pojo.getClass().getDeclaredFields());
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

    private void validate(Object pojo) {
        boolean idPresent = Iterables.tryFind(Arrays.asList(pojo.getClass().getDeclaredFields()),
                input -> input.isAnnotationPresent(Id.class)).isPresent();

        if (!idPresent) {
            throw new RuntimeException("id field isn't present in: " + pojo.getClass()
                    + ", but this class is annotated with Document. You should annotate some field with @Id if you want to save this object in mongo");
        }
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

}

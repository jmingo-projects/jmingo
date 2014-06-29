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
package com.jmingo.util;


import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.jmingo.document.annotation.Document;
import com.jmingo.document.annotation.Id;
import com.jmingo.exceptions.MingoException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.Field;
import java.util.List;

import static com.jmingo.util.ReflectionUtils.getFields;
import static com.jmingo.util.ReflectionUtils.getInheritedFields;

public class DocumentUtils {

    public static String getCollectionName(Object object) {
        return getCollectionName(object.getClass());
    }

    public static String getCollectionName(Class<?> type) {
        String collectionName;
        Document document = type.getAnnotation(Document.class);
        if (StringUtils.isNotBlank(document.collectionName())) {
            collectionName = document.collectionName();
        } else {
            collectionName = type.getSimpleName();
        }
        return collectionName;
    }

    public static Object getIdValue(Object document) {
        Object value = null;
        Validate.notNull(document, "getIdValue: object to get id cannot be null");
        List<Field> currFields = getFields(document.getClass());
        Optional<Field> fieldOptional = getIdField(currFields);
        Field field = fieldOptional.or(getIdField(getInheritedFields(document.getClass()))).orNull();
        if (field != null) {
            field.setAccessible(true);
            try {
                value = field.get(document);
            } catch (IllegalAccessException e) {
                throw Throwables.propagate(e);
            }
        }
        return value;
    }

    private static Optional<Field> getIdField(List<Field> fields) {
        return Iterables.tryFind(fields, field -> field.isAnnotationPresent(Id.class));
    }

    public static void assertDocument(Object object) {
        Validate.notNull(object, "object isn't mingo document because the null");
        assertDocument(object.getClass());
    }

    public static void assertDocument(Class<?> documentType) {
        if (!isDocument(documentType)) {
            throw new MingoException(
                    "[class:"
                            + documentType.getName() +
                            "] is not mingo document because is not annotated with @Document annotation."
            );
        }
    }

    public static boolean isDocument(Class<?> documentType) {
        return documentType != null && documentType.isAnnotationPresent(Document.class);
    }

    public static boolean isId(Field field) {
        return field != null && field.isAnnotationPresent(Id.class);
    }

}

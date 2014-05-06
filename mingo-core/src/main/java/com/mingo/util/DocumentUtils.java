package com.mingo.util;


import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.mingo.annotation.Document;
import com.mingo.annotation.Id;
import com.mingo.exceptions.MingoException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.Field;
import java.util.List;

import static com.mingo.util.ReflectionUtils.getFields;
import static com.mingo.util.ReflectionUtils.getInheritedFields;

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

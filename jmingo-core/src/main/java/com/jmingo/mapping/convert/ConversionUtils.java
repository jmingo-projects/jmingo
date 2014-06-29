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
package com.jmingo.mapping.convert;


import com.google.common.collect.Lists;
import com.jmingo.mapping.convert.mongo.type.TypeTransformer;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.jmingo.util.MongoUtil.toDBObject;

/**
 * Utils class provides convenient methods for convert needs.
 */
public final class ConversionUtils {

    private static final String[] SUPPORTED_DATE_PATTERNS = {"MM-dd-yyyy"};

    private ConversionUtils() {
        throw new UnsupportedOperationException("It's prohibited to create instances of the class.");
    }

    /**
     * Gets first element from source if source has BasicDBList type.
     *
     * @param source source
     * @return first element
     */
    public static DBObject getFirstElement(DBObject source) {
        if (source instanceof BasicDBList) {
            Object first = ((BasicDBList) source).iterator().next();
            if (first instanceof DBObject) {
                source = (DBObject) first;
            } else {
                throw new RuntimeException("element isn't DBObject");
            }
        }
        return source;
    }

    /**
     * Convert aggregation output to BasicDBList.
     *
     * @param aggregationOutput aggregation output
     * @return BasicDBList
     */
    public static BasicDBList getAsBasicDBList(AggregationOutput aggregationOutput) {
        Validate.notNull(aggregationOutput, "aggregation output cannot be null");
        BasicDBList result = new BasicDBList();
        result.addAll(Lists.newArrayList(aggregationOutput.results()));
        return result;
    }

    /**
     * Converts the given source into the list of objects with specified type.
     *
     * @param type      the type of target object
     * @param source    {@link DBObject} interface. expected {@link com.mongodb.BasicDBList} implementation.
     * @param converter converter
     * @param <T>       the type of the class modeled by this {@code Class} object.
     * @return list of converted objects
     */
    public static <T> List<T> convertList(Class<T> type, DBObject source, Converter<T> converter) {
        List<T> list = null;
        Validate.notNull(source, "source cannot be null");
        if (source instanceof BasicDBList) {
            list = convertDBList(type, (BasicDBList) source, converter);

        } else if (source instanceof BasicDBObject) {
            BasicDBList listSource = new BasicDBList();
            listSource.add(source);
            list = convertDBList(type, listSource, converter);

        } else {
            throw new RuntimeException("unsupported source. expected BasicDBList or BasicDBObject.");
        }
        return list;
    }

    private static <T> List<T> convertDBList(Class<T> type, BasicDBList basicDBList, Converter<T> converter) {
        if (CollectionUtils.isEmpty(basicDBList)) {
            return Collections.emptyList();
        }
        return Lists.transform(basicDBList, (item) -> converter.convert(type, castToDbObject(item)));
    }

    /**
     * Convert specified field from source.
     *
     * @param type      they type to convert the given source to.
     * @param field     field name in source. supports using of nested paths: "field1.field2.field3"
     * @param source    source the source to create an object of the given type from.
     * @param converter converter
     * @param <T>       the type of the class modeled by this {@code Class} object.
     * @return converted object
     */
    public static <T> T convertField(Class<T> type, String field, DBObject source, Converter<T> converter) {
        String[] paths = StringUtils.split(field, ".");
        for (String path : paths) {
            DBObject child = getAsDBObject(path, source);
            if (child != null) {
                source = child;
            } else {
                break;
            }
        }
        return converter.convert(type, source);
    }

    /**
     * Return field value as {@link DBObject}.
     *
     * @param field  field name
     * @param source source {@link DBObject}
     * @return field value as {@link DBObject}
     */
    public static DBObject getAsDBObject(String field, DBObject source) {
        if (source != null && StringUtils.isNotEmpty(field)) {
            Object value = source.get(field);
            if (value instanceof DBObject) {
                return (DBObject) value;
            }
        }
        return null;
    }

    /**
     * Return field value as String.
     *
     * @param filed  field field name
     * @param source source {@link DBObject}
     * @return field value as String
     */
    public static String getAsString(String filed, DBObject source) {
        Object value = source.get(filed);
        if (value instanceof String) {
            return (String) value;
        }
        return null;
    }

    /**
     * Return field value as Long.
     *
     * @param filed  field name
     * @param source source {@link DBObject}
     * @return field value as Long
     */
    public static Long getAsLong(String filed, DBObject source) {
        Object value = source.get(filed);
        if (value != null && isLongNumber(value.toString())) {
            return Long.parseLong(value.toString());
        }
        return null;
    }

    /**
     * Return field value as Integer.
     *
     * @param filed  field name
     * @param source source {@link DBObject}
     * @return field value as Integer
     */
    public static Integer getAsInteger(String filed, DBObject source) {
        Object value = source.get(filed);
        if (value != null && isIntNumber(value.toString())) {
            return Integer.parseInt(value.toString());
        }
        return null;
    }

    /**
     * Return specified field value as long.
     *
     * @param field   field in result
     * @param dbItems list of {@link DBObject}
     * @return field as long
     */
    public static Long getAsLong(String field, Iterable<DBObject> dbItems) {
        return getAsObject(field, dbItems, (dbObject, fieldName) -> getAsLong(fieldName, dbObject));
    }

    /**
     * Return specified field value as integer.
     *
     * @param field   field in result
     * @param dbItems list of {@link DBObject}
     * @return field as integer
     */
    public static Integer getAsInteger(String field, Iterable<DBObject> dbItems) {
        return getAsObject(field, dbItems, (dbObject, fieldName) -> getAsInteger(fieldName, dbObject));
    }

    /**
     * Return specified field value as integer.
     *
     * @param field   field in result
     * @param dbItems list of {@link DBObject}
     * @return field as integer
     */
    private static <T> T getAsObject(String field, Iterable<DBObject> dbItems,
                                     TypeTransformer<T> transformer) {
        T value = null;
        for (Object item : dbItems) {
            value = transformer.transform(toDBObject(item), field);
            if (value != null) {
                break;
            }
        }
        return value;
    }

    /**
     * Convert string to date.
     *
     * @param source       date as string
     * @param datePatterns possible date patterns
     * @return date
     * @throws IllegalArgumentException
     */
    public static Date convertToDate(String source, String... datePatterns) {
        try {
            return DateUtils.parseDateStrictly(source, datePatterns);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Wrong date format. Possible formats: " + datePatterns, e);
        }
    }

    /**
     * Convert string to date.
     *
     * @param source date as string
     * @return date
     * @throws IllegalArgumentException
     */
    public static Date convertToDate(String source) {
        return convertToDate(source, SUPPORTED_DATE_PATTERNS);
    }

    /**
     * Check is long.
     *
     * @param value value
     * @return true if long, otherwise - false
     */
    public static boolean isLongNumber(String value) {
        try {
            Long.parseLong(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * Check is int.
     *
     * @param value value
     * @return true if int, otherwise - false
     */
    public static boolean isIntNumber(String value) {
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * Casts given source object to the {@link DBObject} type.
     *
     * @param source the source object to be casted
     * @param <T>    subtype of {@link DBObject}
     * @return object casted to {@link DBObject}
     * @throws IllegalArgumentException if source object isn't instance of DBObject
     */
    public static <T extends DBObject> T castToDbObject(Object source) {
        if (source instanceof DBObject) {
            return (T) source;
        }
        throw new IllegalArgumentException("source object should be instance of: " + DBObject.class);
    }

}

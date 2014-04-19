package com.mingo.query.util;

import static org.slf4j.helpers.MessageFormatter.format;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.mingo.query.Query;
import com.mingo.query.QuerySet;
import com.mongodb.util.JSON;
import com.mongodb.util.JSONParseException;
import com.mongodb.util.JSONSerializers;
import com.mongodb.util.ObjectSerializer;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
public final class QueryUtils {

    private QueryUtils() {
    }

    private static final int COLLECTION_NAME_POSITION = 0;
    private static final int QUERY_ID_POSITION = 1;
    private static final int NUMBER_OF_ELEMENTS = 2;

    /* this prefixed must be placed before parameter name */
    private static final String PARAMETER_PREFIX = "#";
    private static final String PARAMETER_PATTERN = "\"" + PARAMETER_PREFIX + "{}\"";
    private static final String PIPELINE_TEMPLATE = "[{}]";
    private static final String OPERATION_TEMPLATE = "{{}}";
    private static final ObjectSerializer OBJECT_SERIALIZER = JSONSerializers.getLegacy();

    /**
     * Builds composite query ID ith next structure "collectionName.id".
     *
     * @param collectionName collection name
     * @param id             query id
     * @return composite ID - { [collectionName].[id] }
     */
    public static String buildCompositeId(String collectionName, String id) {
        Validate.notBlank(collectionName, "collectionName cannot be null");
        Validate.notBlank(id, "id cannot be null");
        return StringUtils.join(Lists.newArrayList(collectionName, id), ".");
    }


    /**
     * Gets collection name from composite query id.
     *
     * @param compositeQueryId composite query id
     * @return collection name
     */
    public static String getCollectionName(String compositeQueryId) {
        String[] elements = StringUtils.split(compositeQueryId, ".");
        return getElementByPosition(elements, NUMBER_OF_ELEMENTS, COLLECTION_NAME_POSITION);
    }

    /**
     * Gets query id from composite query id.
     *
     * @param compositeQueryId composite query id
     * @return query id
     */
    public static String getQueryId(String compositeQueryId) {
        String[] elements = getCompositeIdElements(compositeQueryId);
        return getElementByPosition(elements, NUMBER_OF_ELEMENTS, QUERY_ID_POSITION);
    }

    /**
     * Gets elements of  composite query id.
     *
     * @param compositeQueryId composite query id
     * @return elements of  composite query id
     */
    public static String[] getCompositeIdElements(String compositeQueryId) {
        return StringUtils.split(compositeQueryId, ".");
    }

    /**
     * Creates composite Id for all queries in querySet.
     *
     * @param querySet {@link com.mingo.query.QuerySet}
     */
    public static void createCompositeIdForQueries(QuerySet querySet) {
        if (MapUtils.isEmpty(querySet.getQueryMap())) {
            return;
        }
        for (Query query : querySet.getQueryMap().values()) {
            query.setCompositeId(buildCompositeId(querySet.getCollectionName(), query.getId()));
        }
    }

    /**
     * Validates composite id.
     *
     * @param compositeId expected format ( [database-name].[collection-name].[query-id] )
     * @throws NullPointerException     {@link NullPointerException}
     * @throws IllegalArgumentException {@link IllegalArgumentException}
     *                                  if composite id is null, empty, contains only spaces or has wrong format.
     */
    public static void validateCompositeId(String compositeId) throws NullPointerException, IllegalArgumentException {
        Validate.notBlank(compositeId, "compositeId cannot be null or empty");
        String[] elements = StringUtils.split(compositeId, ".");
        Validate.isTrue(elements.length == NUMBER_OF_ELEMENTS, "composite id should consist of " + NUMBER_OF_ELEMENTS + " parts: " +
                "([collection-name].[query-id]). current value: " + compositeId);
        for (int index = 0; index < elements.length; index++) {
            Validate.notBlank(elements[index], "element with position: " + (index + 1) +
                    " in composite id is empty. current value: " + compositeId);
        }
    }

    private static String getElementByPosition(String[] elements, int numberOfElements, int pos) {
        if (elements != null && elements.length == numberOfElements && pos < numberOfElements) {
            return elements[pos];
        } else {
            return null;
        }
    }

    /**
     * Checks what query has valid format.
     *
     * @param query query
     * @return true if query is correct otherwise - false
     */
    public static boolean validate(String query) {
        boolean valid = true;
        try {
            JSON.parse(query);
        } catch (JSONParseException e) {
            valid = false;
        }
        return valid;
    }

    /**
     * Wraps query in [] brackets.
     *
     * @param query query
     * @return wrapped query
     */
    public static String wrap(String query) {
        return format(PIPELINE_TEMPLATE, query).getMessage();
    }

    /**
     * Wraps query in {} brackets.
     *
     * @param query query
     * @return wrapped query
     */
    public static String wrapInBracket(String query) {
        return format(OPERATION_TEMPLATE, query).getMessage();
    }

    /**
     * Replace parameters in query.
     *
     * @param query      query
     * @param parameters parameters
     * @return prepared query
     */
    public static String replaceQueryParameters(String query, Map<String, Object> parameters) {
        if (StringUtils.isNotBlank(query) && MapUtils.isNotEmpty(parameters)) {
            for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
                if (StringUtils.isNotEmpty(parameter.getKey())) {
                    if (isParameterEmpty(parameter.getValue())) {
                        query = StringUtils.replace(query, PARAMETER_PREFIX + parameter.getKey(), StringUtils.EMPTY);
                    } else {
                        String replacement = createReplacement(parameter.getKey(), parameter.getValue());
                        query = StringUtils.replace(query, replacement,
                                getParameterAsString(parameter.getValue()));
                    }
                }
            }
        }
        return removeParameters(query);
    }


    private static String removeParameters(String query) {
        if (StringUtils.isBlank(query)) {
            return query;
        }
        int paramPrefixIndex = query.indexOf(PARAMETER_PREFIX);
        if (paramPrefixIndex > 0) {
            return removeParameters(query.replace(getParameterPlaceholder(paramPrefixIndex, query), StringUtils.EMPTY));

        }
        return query;
    }

    private static String getParameterPlaceholder(int paramPrefixIndex, String query) {
        for (int i = paramPrefixIndex; i < query.length(); i++) {
            if ("\"".equals(String.valueOf(query.charAt(i)))) {
                return query.substring(paramPrefixIndex, i);
            }
        }
        return StringUtils.EMPTY;
    }

    private static String getParameterAsString(Object parameter) {
        if (isParameterEmpty(parameter)) {
            return StringUtils.EMPTY;
        }
        String val;
        if (isTypeSupported(parameter)) {
            if (parameter instanceof Collection) {
                val = toString((Collection) parameter);
            } else if (parameter instanceof Object[]) {
                val = toString((Object[]) parameter);
            } else if (parameter instanceof Date) {
                val = OBJECT_SERIALIZER.serialize(parameter);
            } else {
                val = parameter.toString();
            }
        } else {
            throw new RuntimeException("unsupported parameter type. parameter value: " +
                    parameter + ", class: " + parameter.getClass());
        }
        return val;
    }

    private static String toString(Object[] array) {
        return toString(Lists.newArrayList(array));
    }

    private static String toString(Collection<?> collection) {
        List<String> values = Lists.transform(Lists.newArrayList(collection), new Function<Object, String>() {
            @Override
            public String apply(Object input) {
                return "'" + input.toString() + "'";
            }
        });
        return "[" + StringUtils.join(values, ",") + "]";
    }

    private static boolean isParameterEmpty(Object val) {
        return !(isParameterNotEmpty(val));
    }

    private static boolean isParameterNotEmpty(Object val) {
        if (val == null) {
            return false;
        }
        if (val instanceof Collection) {
            return !((Collection) val).isEmpty();
        } else if (val instanceof Object[]) {
            return ((Object[]) val).length > 0;
        } else if (val instanceof String) {
            return StringUtils.isNotEmpty((String) val);
        }
        return true;
    }

    private static String createReplacement(String name, Object value) {
        return (isFullReplace(value)) ? format(PARAMETER_PATTERN, name).getMessage() : PARAMETER_PREFIX + name;
    }

    private static boolean isFullReplace(Object parameter) {
        return parameter instanceof Collection ||
                parameter instanceof Object[] ||
                parameter instanceof Number ||
                parameter instanceof Date;
    }

    private static boolean isTypeSupported(Object val) {
        return val instanceof String
                || val instanceof Number
                || val instanceof Date
                || val instanceof Collection
                || val instanceof Object[]
                || val instanceof Enum;
    }

}

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

import com.fasterxml.jackson.core.JsonParser;
import com.google.common.collect.Lists;
import com.jmingo.mapping.marshall.jackson.MongoMapper;
import com.mongodb.util.JSONSerializers;
import com.mongodb.util.ObjectSerializer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.io.IOException;

import static org.slf4j.helpers.MessageFormatter.format;

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
        Validate.isTrue(elements.length == NUMBER_OF_ELEMENTS,
                "composite id should consist of " + NUMBER_OF_ELEMENTS + " parts: " +
                        "([collection-name].[query-id]). current value: " + compositeId
        );
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
     * Checks what string has valid json format.
     *
     * @param json the json to validate
     * @return true if string is correct json otherwise - false
     */
    public static boolean isValidJSON(final String json) {
        boolean valid = false;
        try {
            final JsonParser parser = new MongoMapper().getFactory()
                    .createParser(json);
            while (parser.nextToken() != null) {
            }
            valid = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return valid;
    }

    /**
     * Wraps query in [] brackets.
     *
     * @param query query
     * @return wrapped query
     */
    public static String pipeline(String query) {
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

}

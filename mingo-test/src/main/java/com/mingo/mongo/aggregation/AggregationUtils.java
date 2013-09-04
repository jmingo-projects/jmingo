package com.mingo.mongo.aggregation;

import static com.mingo.convert.ConversionUtils.convertToDate;
import static com.mingo.mongo.aggregation.AggregationOperators.GT;
import static com.mingo.mongo.aggregation.AggregationOperators.LT;
import static com.mongodb.BasicDBObjectBuilder.start;
import static org.apache.commons.lang3.time.DateUtils.addDays;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObjectBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Map;

/**
 * Copyright 2012-2013 The Mingo Team
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public final class AggregationUtils {

    private AggregationUtils() {
        throw new UnsupportedOperationException("It's prohibited to create instances of the class.");
    }

    private static final String START_DATE = "startdate";
    private static final String END_DATE = "enddate";
    private static final String SIMPLE_PREFIX = "$";

    /**
     * Add field to builder if exist in  parameters.
     *
     * @param builder    {@link BasicDBObjectBuilder}
     * @param fieldName  field name
     * @param parameters parameters
     */
    public static void appendField(BasicDBObjectBuilder builder, String fieldName, Map<String, String> parameters) {
        if (MapUtils.isNotEmpty(parameters) && parameters.containsKey(fieldName)) {
            builder.add(fieldName, parameters.get(fieldName));
        }
    }

    /**
     * Add fields to builder with $ prefix.
     *
     * @param builder {@link BasicDBObjectBuilder}
     * @param fields  fields
     */
    public static void appendFieldWithSimplePrefix(BasicDBObjectBuilder builder, String... fields) {
        if (CollectionUtils.isNotEmpty(Lists.newArrayList(fields))) {
            for (String field : fields) {
                appendFieldWithSimplePrefix(builder, field);
            }
        }
    }

    /**
     * Add field to builder with $ prefix.
     *
     * @param builder {@link BasicDBObjectBuilder}
     * @param field   field
     */
    public static void appendFieldWithSimplePrefix(BasicDBObjectBuilder builder, String field) {
        if (StringUtils.isNotEmpty(field)) {
            builder.add(field, SIMPLE_PREFIX + field);
        }
    }

    /**
     * Added condition for created field.
     *
     * @param builder    {@link BasicDBObjectBuilder}
     * @param field      field
     * @param parameters parameters
     */
    public static void addBetweenCriteria(BasicDBObjectBuilder builder, String field,
                                          Map<String, String> parameters) {
        BasicDBObjectBuilder createdBuilder = start();
        Date startRange = parameters.containsKey(START_DATE) ? convertToDate(parameters.get(START_DATE)) : null;
        Date endRange = parameters.containsKey(END_DATE) ? addDays(convertToDate(parameters.get(END_DATE)), 1) : null;

        if (startRange != null) {
            createdBuilder.add(GT.getMongoName(), startRange);
        }
        if (endRange != null) {
            createdBuilder.add(LT.getMongoName(), endRange);
        }
        if (!createdBuilder.isEmpty()) {
            builder.add(field, createdBuilder.get());
        }
    }

}

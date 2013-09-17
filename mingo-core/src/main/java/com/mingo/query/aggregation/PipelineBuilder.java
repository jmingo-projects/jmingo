package com.mingo.query.aggregation;

import static com.mingo.query.util.QueryUtils.wrap;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.mongodb.util.JSONCallback;
import net.jcip.annotations.ThreadSafe;
import org.apache.commons.lang3.StringUtils;
import org.bson.BSONCallback;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

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
@ThreadSafe
@Deprecated /* use com.mingo.query.parser.QueryParser */
public class PipelineBuilder {

    private static final String DEFAULT_QUERY = "{$match : {}}";

    private BSONCallback bsonCallback = new CustomJSONCallback();

    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Builds find criteria for plain query. Is thread safe.
     *
     * @param json plain query : {field1:value1, field2:value2, ...}
     *             if value is null or empty then condition
     *             for filed with that value will be removed from criteria.
     *             Example:
     *             source: { field1 : "value1", field2:""}
     *             after build: { field1 : "value1" }
     * @return find criteria
     */
    public DBObject build(String json) {
        lock.lock();
        try {
            return (DBObject) JSON.parse(json, bsonCallback);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Builds operators pipeline for aggregation query. Is thread safe.
     * Conditions with empty values will be removed from pipeline.
     * Example:
     * source: {$match : { "field1": { $in: []}, "field2": { "$gt" : "value2"}}}
     * after build:[{$match : {field2": { "$gt" : "value2"}}}]
     * <p/>
     * source: {$match : { "field1": { $in: [value1, value2]}, "field2": { "$gt" : ""}}}
     * after build:[ {$match : { "field1": { $in: [value1, value2]}}} ]
     *
     * @param json query
     * @return operators
     */
    public BasicDBList buildAggregation(String json) {
        lock.lock();
        try {
            BasicDBList operators = (BasicDBList) JSON.parse(wrap(json), bsonCallback);
            return operators.isEmpty() ? (BasicDBList) JSON.parse(wrap(DEFAULT_QUERY)) : operators;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Serialize basicDBList to json.
     *
     * @param basicDBList {@link BasicDBList}
     * @return json
     */
    public String serialize(BasicDBList basicDBList) {
        return JSON.serialize(basicDBList);
    }

    /**
     * Remove operators without values.
     */
    private class CustomJSONCallback extends JSONCallback {

        @Override
        public void gotString(String name, String v) {
            // exclude fields with empty parameters
            if (StringUtils.isNotEmpty(v)) {
                super.gotString(name, v);
            }
        }

        @Override
        protected void _put(String name, Object val) {
            if (isElementNotEmpty(val)) {
                super._put(name, val);
            }
        }

        @Override
        public Object objectDone() {
            String name = curName();
            Object done = super.objectDone();

            if (name != null && !isElementNotEmpty(done)) {
                return cur().removeField(name);
            }
            return done;
        }
    }

    private boolean isElementNotEmpty(Object value) {
        if (value instanceof BasicDBObject) {
            BasicDBObject dbObjVal = (BasicDBObject) value;
            if (dbObjVal.isEmpty()) {
                return false;
            }
            for (Map.Entry<String, Object> entry : dbObjVal.entrySet()) {
                boolean tmp = isElementNotEmpty(entry.getValue());
                if (!tmp) {
                    return false;
                } else {
                    continue;
                }
            }
        }
        return true;
    }

}

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
package org.jmingo.mapping.marshall.mongo;

import org.jmingo.exceptions.MarshallingException;
import org.jmingo.mapping.marshall.JsonToDBObjectMarshaller;
import org.jmingo.mapping.marshall.mongo.callback.BasicDBListReplacementCallback;
import org.jmingo.mapping.marshall.mongo.callback.BasicDBObjectReplacementCallback;
import org.jmingo.mapping.marshall.mongo.callback.ReplacementCallback;
import org.jmingo.mapping.marshall.mongo.callback.SimpleObjectReplacementCallback;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.mongodb.util.JSONCallback;
import org.bson.BSON;

import java.util.Map;

/**
 * Implementation of {@link JsonToDBObjectMarshaller} based on {@link JSON}.
 * Has ability to replace parameters in json with specified replacements.
 */
public class MongoJsonToDBObjectMarshaller implements JsonToDBObjectMarshaller {

    /**
     * Default constructor.
     */
    public MongoJsonToDBObjectMarshaller() {

        // fix for https://jira.mongodb.org/browse/JAVA-268
        BSON.addEncodingHook(Enum.class, (val) -> {
            if (val != null && val.getClass().isEnum()) {
                return ((Enum) val).name();
            } else {
                return val;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DBObject marshall(String json, Map<String, Object> parameters) throws MarshallingException {
        return (DBObject) JSON.parse(json, new BSONCallback(parameters));
    }

    /**
     * This callback is called for each element in json during parsing and applies specific implementation of
     * {@link org.jmingo.mapping.marshall.mongo.callback.ReplacementCallback} for each json element based on element type.
     */
    private static class BSONCallback extends JSONCallback {
        private ReplacementCallback<BasicDBObject> basicDBObjectReplacementCallback;
        private ReplacementCallback<BasicDBList> basicDBListReplacementCallback;
        private ReplacementCallback<Object> simpleObjectReplacementCallback;

        public BSONCallback(Map<String, Object> parameters) {
            basicDBObjectReplacementCallback = new BasicDBObjectReplacementCallback(parameters);
            basicDBListReplacementCallback = new BasicDBListReplacementCallback(parameters);
            simpleObjectReplacementCallback = new SimpleObjectReplacementCallback(parameters);
        }

        @Override
        public Object objectDone() {
            Object source = super.objectDone();
            doReplace(source);
            return source;
        }

        private Object doReplace(Object source) {
            if (source instanceof BasicDBObject) {
                return basicDBObjectReplacementCallback.doReplace((BasicDBObject) source);
            }
            if (source instanceof BasicDBList) {
                return basicDBListReplacementCallback.doReplace((BasicDBList) source);
            }
            return simpleObjectReplacementCallback.doReplace(source);
        }
    }
}

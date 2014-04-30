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
package com.mingo.marshall.mongo;

import com.mingo.exceptions.MarshallingException;
import com.mingo.marshall.AbstractJsonToBsonMarshaller;
import com.mingo.marshall.JsonToDBObjectMarshaller;
import com.mingo.query.replacer.BasicDBListReplacementCallback;
import com.mingo.query.replacer.BasicDBObjectReplacementCallback;
import com.mingo.query.replacer.ReplacementCallback;
import com.mingo.query.replacer.SimpleObjectReplacementCallback;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.mongodb.util.JSONCallback;

import java.util.Map;

public class MongoJsonToDBObjectMarshaller extends AbstractJsonToBsonMarshaller<DBObject> implements JsonToDBObjectMarshaller {

    @Override
    public DBObject marshall(String json, Map<String, Object> parameters) throws MarshallingException {
        return (DBObject) JSON.parse(json, new BSONCallback(parameters));
    }

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

package com.mingo.query.parser.json;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.mongodb.util.JSONCallback;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.lang3.StringUtils;
import org.bson.BSONCallback;

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
 * <p/>
 * This class is implementation of {@link com.mingo.query.parser.json.JsonParser} interface.
 */
public class EscapeParser implements JsonParser {

    private BSONCallback bsonCallback = new CustomJSONCallback();

    private final ReentrantLock lock = new ReentrantLock();

    /**
     * {@inheritDoc}.
     */
    @Override
    public DBObject parse(String json) {
        lock.lock();
        try {
            return (DBObject) JSON.parse(json, bsonCallback);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Remove operators without values.
     */
    private class CustomJSONCallback extends JSONCallback {

        @Override
        public void gotString(String name, String v) {
            // exclude fields with empty parameters
            if(StringUtils.isNotEmpty(v)) {
                super.gotString(name, v);
            }
        }

        @Override
        protected void _put(String name, Object val) {
            if(isElementNotEmpty(val)) {
                super._put(name, val);
            }
        }

        @Override
        public Object objectDone() {
            String name = curName();
            Object done = super.objectDone();

            if(name != null && !isElementNotEmpty(done)) {
                return cur().removeField(name);
            }
            return done;
        }
    }

    private boolean isElementNotEmpty(Object value) {
        if(value instanceof BasicDBObject) {
            BasicDBObject dbObjVal = (BasicDBObject) value;
            if(dbObjVal.isEmpty()) {
                return false;
            }
            for(Map.Entry<String, Object> entry : dbObjVal.entrySet()) {
                boolean tmp = isElementNotEmpty(entry.getValue());
                if(!tmp) {
                    return false;
                } else {
                    continue;
                }
            }
        }
        return true;
    }

}

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
package org.jmingo.mapping.marshall.mongo.callback;

import com.mongodb.BasicDBObject;

import java.util.Map;

/**
 * Callback to replace values in {@link BasicDBObject}.
 */
public class BasicDBObjectReplacementCallback implements ReplacementCallback<BasicDBObject> {

    private ReplacementCallback<Map> mapReplacementCallback;

    /**
     * Constructor this parameters.
     *
     * @param replacements the replacements
     */
    public BasicDBObjectReplacementCallback(Map<String, Object> replacements) {
        mapReplacementCallback = new MapReplacementCallback(replacements);
    }

    /**
     * Replace all values in dbObject with replacements.
     *
     * @param dbObject the dbObject to replace
     * @return dbObject with replaced values
     */
    @Override
    public Object doReplace(BasicDBObject dbObject) {
        if (dbObject != null) {
            mapReplacementCallback.doReplace(dbObject);
        }
        return dbObject;
    }
}

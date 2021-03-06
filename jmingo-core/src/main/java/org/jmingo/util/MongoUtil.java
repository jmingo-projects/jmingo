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
package org.jmingo.util;

import com.mongodb.DBObject;


public final class MongoUtil {

    /**
     * Cast any object to DBObject or to inheritors.
     *
     * @param obj object
     * @param <T> any type which instance of DBObject
     * @return casted object
     */
    @SuppressWarnings(value = "unchecked")
    public static <T extends DBObject> T toDBObject(Object obj) {
        if (obj instanceof DBObject) {
            return (T) obj;
        }
        return null;
    }

}

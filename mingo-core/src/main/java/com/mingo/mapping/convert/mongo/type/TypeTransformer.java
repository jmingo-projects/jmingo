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
package com.mingo.mapping.convert.mongo.type;

import com.mongodb.DBObject;

/**
 * Functional interface to get a value from dbObject and transform to object with specified type T.
 *
 * @param <T> the type of the class modeled by this {@code Class} object.
 */
@FunctionalInterface
public interface TypeTransformer<T> {

    /**
     * Transforms value from <code>dbObject</code> by key = <code>fieldName</code>
     * to object with specified type T.
     *
     * @param dbObject  DBObject
     * @param fieldName filed name
     * @return object with specified type
     */
    T transform(DBObject dbObject, String fieldName);
}

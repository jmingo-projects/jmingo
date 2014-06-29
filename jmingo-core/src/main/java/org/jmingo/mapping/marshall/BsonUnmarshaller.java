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
package org.jmingo.mapping.marshall;


import org.jmingo.exceptions.MarshallingException;
import org.bson.BSONObject;

/**
 * Unmarshall BSON objects into an objects.
 */
public interface BsonUnmarshaller {

    /**
     * Unmarshall the given BSON object into an object with specified type.
     *
     * @param type   the type of the result object
     * @param source the BSON object to unmarshall
     * @return unmarshalled BSON object
     * @throws MarshallingException if any marshalling errors occur
     */
    <T, B extends BSONObject> T unmarshall(Class<T> type, B source) throws MarshallingException;
}

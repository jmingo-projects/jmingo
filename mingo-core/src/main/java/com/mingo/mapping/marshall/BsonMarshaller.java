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
package com.mingo.mapping.marshall;


import com.mingo.exceptions.MarshallingException;
import org.bson.BSONObject;

/**
 * Marshall an objects into BSON objects.
 */
public interface BsonMarshaller {

    /**
     * Marshall the given pojo into BSON object.
     *
     * @param type the concrete BSON type of object into which the pojo should be marshalled
     * @param pojo the pojo to marshall
     * @return pojo marshalled into a BSON object with specified type
     * @throws MarshallingException if any marshalling errors occur
     */
    <T extends BSONObject> T marshall(Class<T> type, Object pojo) throws MarshallingException;
}

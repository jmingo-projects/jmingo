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
package com.jmingo.mapping.marshall;


import com.jmingo.exceptions.MarshallingException;
import org.bson.BSONObject;

import java.util.Collections;
import java.util.Map;

/**
 * Marshaller to marshall json into BSON object.
 */
public interface JsonToBsonMarshaller<T extends BSONObject> {

    /**
     * Marshall json into BSON object.
     *
     * @param json json to marshall
     * @return BSON object
     * @throws MarshallingException if any marshalling errors occur
     */
    default T marshall(String json) throws MarshallingException {
        return marshall(json, Collections.emptyMap());
    }

    /**
     * Marshall json into BSON object.
     *
     * @param json       json to marshall
     * @param parameters the parameters are used to replace the values in json that start with specific prefix '#' and
     *                   present in parameters names
     * @return BSON object
     * @throws MarshallingException if any marshalling errors occur
     */
    T marshall(String json, Map<String, Object> parameters) throws MarshallingException;
}

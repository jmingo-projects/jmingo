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
package com.jmingo.mapping.marshall.mongo;

import com.jmingo.mapping.marshall.BsonMarshaller;
import com.jmingo.mapping.marshall.BsonMarshallingFactory;
import com.jmingo.mapping.marshall.BsonUnmarshaller;
import com.jmingo.mapping.marshall.JsonToDBObjectMarshaller;

/**
 * This implementation is used to create marshallers based on Mongo JSON.
 */
public class MongoBsonMarshallingFactory implements BsonMarshallingFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public BsonMarshaller createMarshaller() {
        throw new UnsupportedOperationException("MongoBsonMarshallingFactory::createMarshaller(), there are no implementations of BsonMarshaller to create");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BsonUnmarshaller createUnmarshaller() {
        throw new UnsupportedOperationException("MongoBsonMarshallingFactory::createUnmarshaller(), there are no implementations of BsonUnmarshaller to create");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonToDBObjectMarshaller createJsonToDbObjectMarshaller() {
        return new MongoJsonToDBObjectMarshaller();
    }

}

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
package org.jmingo.mapping.marshall.jackson;

import org.jmingo.mapping.marshall.BsonMarshaller;
import org.jmingo.mapping.marshall.BsonMarshallingFactory;
import org.jmingo.mapping.marshall.BsonUnmarshaller;
import org.jmingo.mapping.marshall.JsonToDBObjectMarshaller;

/**
 * This implementation is used to create marshallers based on Jackson.
 */
public class JacksonBsonMarshallingFactory implements BsonMarshallingFactory {

    private static final MongoMapper MONGO_MAPPER = new MongoMapper();

    /**
     * {@inheritDoc}
     */
    @Override
    public BsonMarshaller createMarshaller() {
        return new JacksonBsonMarshaller(MONGO_MAPPER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BsonUnmarshaller createUnmarshaller() {
        return new JacksonBsonUnmarshaller(MONGO_MAPPER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonToDBObjectMarshaller createJsonToDbObjectMarshaller() {
        throw new UnsupportedOperationException("There are no Jackson implementation of JsonToDBObjectMarshaller");
    }

}

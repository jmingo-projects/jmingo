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
package com.mingo.mapping.marshall.jackson;

import com.mingo.mapping.marshall.BsonMarshaller;
import com.mingo.mapping.marshall.BsonMarshallingFactory;
import com.mingo.mapping.marshall.BsonUnmarshaller;
import com.mingo.mapping.marshall.JsonToDBObjectMarshaller;

public class JacksonBsonMarshallingFactory implements BsonMarshallingFactory {

    private static final MongoMapper MONGO_MAPPER = new MongoMapper();

    @Override
    public BsonMarshaller createMarshaller() {
        return new JacksonBsonMarshaller(MONGO_MAPPER);
    }

    @Override
    public BsonUnmarshaller createUnmarshaller() {
        return new JacksonBsonUnmarshaller(MONGO_MAPPER);
    }

    @Override
    public JsonToDBObjectMarshaller createJsonToDbObjectMarshaller() {
        throw new UnsupportedOperationException("There are no Jackson implementation of JsonToDBObjectMarshaller");
    }

}

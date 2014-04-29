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
package com.mingo.marshall.jackson;

import com.mingo.marshall.BsonMarshaller;
import com.mingo.marshall.MarshallPreProcessor;
import org.bson.BSONObject;

public class JacksonBsonMarshaller implements BsonMarshaller {

    private MongoMapper mongoMapper;
    private MarshallPreProcessor marshallPreProcessor;

    public JacksonBsonMarshaller(MongoMapper mongoMapper, MarshallPreProcessor marshallPreProcessor) {
        this.mongoMapper = mongoMapper;
        this.marshallPreProcessor = marshallPreProcessor;
    }

    protected Object _prepare(Object pojo) {
        return marshallPreProcessor.process(pojo);
    }

    @Override
    public <T extends BSONObject> T marshall(Class<T> type, Object pojo) throws RuntimeException {
        _prepare(pojo);
        return mongoMapper.convertValue(pojo, type);
    }
}

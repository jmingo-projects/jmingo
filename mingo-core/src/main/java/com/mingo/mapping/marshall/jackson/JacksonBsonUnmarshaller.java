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

import com.mingo.mapping.marshall.BsonUnmarshaller;
import org.bson.BSONObject;


public class JacksonBsonUnmarshaller implements BsonUnmarshaller {

    private MongoMapper mongoMapper;

    public JacksonBsonUnmarshaller(MongoMapper mongoMapper) {
        this.mongoMapper = mongoMapper;
    }

    @Override
    public <T, B extends BSONObject> T unmarshall(Class<T> type, B source) {
        return mongoMapper.convertValue(source, type);
    }
}

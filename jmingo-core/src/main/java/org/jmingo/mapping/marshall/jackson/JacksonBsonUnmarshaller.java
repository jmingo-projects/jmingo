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

import org.jmingo.exceptions.MarshallingException;
import org.jmingo.mapping.marshall.BsonUnmarshaller;
import org.bson.BSONObject;

/**
 * Implementation of {@link org.jmingo.mapping.marshall.BsonUnmarshaller} based on Jackson json lib.
 */
public class JacksonBsonUnmarshaller implements BsonUnmarshaller {

    private MongoMapper mongoMapper;

    /**
     * Constructor with parameters.
     *
     * @param mongoMapper the mongo mapper
     */
    public JacksonBsonUnmarshaller(MongoMapper mongoMapper) {
        this.mongoMapper = mongoMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T, B extends BSONObject> T unmarshall(Class<T> type, B source) throws MarshallingException {
        T result;
        try {
            result = mongoMapper.convertValue(source, type);
        } catch (RuntimeException ex) {
            throw new MarshallingException(ex);
        }
        return result;
    }

}

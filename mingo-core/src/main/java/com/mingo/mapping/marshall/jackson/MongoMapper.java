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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mingo.mapping.convert.mongo.type.deserialize.MongoDateDeserializer;
import com.mingo.mapping.convert.mongo.type.deserialize.ObjectIdDeserializer;
import com.mingo.mapping.convert.mongo.type.serialize.BsonDateSerializer;
import com.mingo.mapping.convert.mongo.type.serialize.ObjectIdSerializer;
import com.mingo.mapping.marshall.jackson.mixin.DBObjectMixIn;
import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;

import java.util.Date;

public class MongoMapper extends ObjectMapper {

    public MongoMapper() {
        addMixInAnnotations();
        setVisibilityChecker(getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        SimpleModule mongoModule = new SimpleModule();
        mongoModule.addDeserializer(ObjectId.class, new ObjectIdDeserializer());
        mongoModule.addSerializer(ObjectId.class, new ObjectIdSerializer());
        mongoModule.addDeserializer(Date.class, new MongoDateDeserializer());
        mongoModule.addSerializer(Date.class, new BsonDateSerializer());
        registerModule(mongoModule);
    }

    private void addMixInAnnotations() {
        addMixInAnnotations(BasicDBObject.class, DBObjectMixIn.class);
    }

}

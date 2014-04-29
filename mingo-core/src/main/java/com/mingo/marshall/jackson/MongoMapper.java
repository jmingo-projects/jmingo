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

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import org.bson.BSONObject;

import java.util.Map;

public class MongoMapper extends ObjectMapper {

    private Map<Class<? extends BSONObject>, Class<?>> bsonObjectMixIns = Maps.newHashMap();

    protected void _defineMixIns() {
        bsonObjectMixIns.put(BasicDBObject.class, DBObjectMixIn.class);
    }

    public MongoMapper() {
        _defineMixIns();
        //register mix in
        bsonObjectMixIns.forEach(this::addMixInAnnotations);
        getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE);
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

    public MongoMapper(Module module) {
        this();
        registerModule(module);
    }


    public interface DBObjectMixIn {
        @JsonAnySetter
        void put(String key, Object value);
    }

}

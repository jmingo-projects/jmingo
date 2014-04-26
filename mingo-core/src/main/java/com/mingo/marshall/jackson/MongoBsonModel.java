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

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mingo.convert.mongo.type.deserialize.ObjectIdDeserializer;
import com.mingo.convert.mongo.type.serialize.ObjectIdSerializer;
import org.bson.types.ObjectId;

public class MongoBsonModel extends SimpleModule {

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        addDeserializer(ObjectId.class, new ObjectIdDeserializer());
        addSerializer(ObjectId.class, new ObjectIdSerializer());


    }

}

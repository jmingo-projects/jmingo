/**
 * Copyright 2012-2013 The Mingo Team
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mingo.convert.mongo.type.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import java.io.IOException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * This converter is used only if the mongodb JSON.serialize is used.
 */
public class MongoDateDeserializer extends StdScalarDeserializer<Date> {

    private static final String DATE_TYPE = "$date";

    private static final ISO8601DateFormat ISO_8601_DATE_FORMAT = new ISO8601DateFormat();

    /**
     * Default converter.
     */
    public MongoDateDeserializer() {
        super(Date.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode tree = jp.readValueAsTree();
        Iterator<Map.Entry<String, JsonNode>> fieldNameIt = tree.fields();
        while (fieldNameIt.hasNext()) {
            Map.Entry<String, JsonNode> entry = fieldNameIt.next();
            if (DATE_TYPE.equals(entry.getKey())) {
                return ISO_8601_DATE_FORMAT.parse(entry.getValue().asText(), new ParsePosition(0));
            }
        }
        return null;
    }

}

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
package com.mingo.mapping.convert.mongo.type.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.node.POJONode;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.google.common.base.Throwables;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

/**
 * This converter is used by default to convert string representation of bson date into {@link Date}.
 */
public class MongoDateDeserializer extends StdScalarDeserializer<Date> {

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
        if (tree.isPojo()) {
            POJONode pojoNode = (POJONode) tree;
            Object pojo = pojoNode.getPojo();

            if (pojo instanceof Date) {
                return (Date) pojoNode.getPojo();
            } else {
                throw new RuntimeException("unsupported date type, expected: " + Date.class.getName());
            }
        }
        String stringDate = tree.asText();
        StdDateFormat stdDateFormat = new StdDateFormat();
        try {
            return stdDateFormat.parse(stringDate);
        } catch (ParseException e) {
            throw Throwables.propagate(e);
        }

    }

}

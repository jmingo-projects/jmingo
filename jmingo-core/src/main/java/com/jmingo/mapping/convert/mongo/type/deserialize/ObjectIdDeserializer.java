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
package com.jmingo.mapping.convert.mongo.type.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.bson.types.ObjectId;

import java.io.IOException;

/**
 * Json deserializer to deserialize {@link ObjectId}.
 */
public class ObjectIdDeserializer extends JsonDeserializer<ObjectId> {

    public static final String MONGO_OID = "$oid";

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectId deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        TreeNode treeNode = jp.readValueAsTree();
        JsonNode oid = ((JsonNode) treeNode).get(MONGO_OID);
        if (oid != null)
            return getAsObjectId(oid);
        else {
            return getAsObjectId((JsonNode) treeNode);
        }
    }

    private ObjectId getAsObjectId(JsonNode treeNode) {

        String text = treeNode.asText();
        boolean valide = ObjectId.isValid(text);
        if (!valide) {
            throw new RuntimeException("failed to create ObjectId from: " + text);
        }
        return new ObjectId(text);
    }

}

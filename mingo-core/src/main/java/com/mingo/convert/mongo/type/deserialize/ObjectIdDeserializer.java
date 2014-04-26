package com.mingo.convert.mongo.type.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.bson.types.ObjectId;

import java.io.IOException;

public class ObjectIdDeserializer extends JsonDeserializer<ObjectId> {
    public static final String MONGO_OID = "$oid";

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

package com.mingo.mapping.marshall.mongo;

import com.mingo.mapping.marshall.BsonMarshaller;
import com.mingo.mapping.marshall.BsonMarshallingFactory;
import com.mingo.mapping.marshall.BsonUnmarshaller;
import com.mingo.mapping.marshall.JsonToDBObjectMarshaller;

public class MongoBsonMarshallingFactory implements BsonMarshallingFactory {

    private static final JsonToDBObjectMarshaller JSON_TO_DB_OBJECT_MARSHALLER = new MongoJsonToDBObjectMarshaller();

    private static final BsonMarshallingFactory instance = new MongoBsonMarshallingFactory();

    public static BsonMarshallingFactory getInstance() {
        return instance;
    }

    @Override
    public BsonMarshaller createMarshaller() {
        throw new UnsupportedOperationException("MongoBsonMarshallingFactory::createMarshaller(), there are no implementations of BsonMarshaller to create");
    }

    @Override
    public BsonUnmarshaller createUnmarshaller() {
        throw new UnsupportedOperationException("MongoBsonMarshallingFactory::createUnmarshaller(), there are no implementations of BsonUnmarshaller to create");
    }

    @Override
    public JsonToDBObjectMarshaller createJsonToDbObjectMarshaller() {
        return JSON_TO_DB_OBJECT_MARSHALLER;
    }

}

package com.mingo.mapping.marshall;


public interface BsonMarshallingFactory {

    BsonMarshaller createMarshaller();

    BsonUnmarshaller createUnmarshaller();

    JsonToDBObjectMarshaller createJsonToDbObjectMarshaller();
}

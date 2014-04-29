package com.mingo.marshall;


public interface BsonMarshallingFactory {

    BsonMarshaller createMarshaller();

    BsonUnmarshaller createUnmarshaller();

    JsonToDBObjectMarshaller createJsonToDbObjectMarshaller();
}

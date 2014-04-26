package com.mingo.marshall;


public interface BsonMarshallingFactory {

    MongoBsonMarshaller createMarshaller();

    MongoBsonUnmarshaller createUnmarshaller();
}

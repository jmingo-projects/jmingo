package com.mingo.convert.mongo.type.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Date;

/**
 * This serializer is used by default and writes java date as is.
 */
public class BsonDateSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        // writes date as is and leaves mongo driver to convert date in appropriate format.
        // Mongodb represents date in ISO format and saves date in ISODate
        jgen.writeObject(value);
    }
}




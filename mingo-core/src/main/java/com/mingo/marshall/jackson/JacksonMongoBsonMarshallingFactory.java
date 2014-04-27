package com.mingo.marshall.jackson;


import com.fasterxml.jackson.databind.SerializationFeature;
import com.mingo.convert.mongo.type.deserialize.MongoDateDeserializer;
import com.mingo.convert.mongo.type.deserialize.ObjectIdDeserializer;
import com.mingo.convert.mongo.type.serialize.BsonDateSerializer;
import com.mingo.convert.mongo.type.serialize.ObjectIdSerializer;
import com.mingo.marshall.BsonMarshallingFactory;
import com.mingo.marshall.MarshallPreProcessor;
import com.mingo.marshall.MongoBsonMarshaller;
import com.mingo.marshall.MongoBsonUnmarshaller;
import org.bson.types.ObjectId;

import java.util.Date;

public class JacksonMongoBsonMarshallingFactory implements BsonMarshallingFactory {
    private static final MongoBsonModel mongoBsonModel = new MongoBsonModel();

    static {
        mongoBsonModel.addDeserializer(ObjectId.class, new ObjectIdDeserializer());
        mongoBsonModel.addSerializer(ObjectId.class, new ObjectIdSerializer());
        mongoBsonModel.addDeserializer(Date.class, new MongoDateDeserializer());
        mongoBsonModel.addSerializer(Date.class, new BsonDateSerializer());
    }

    private static final MongoMapper mongoMapper = new MongoMapper(mongoBsonModel);

    static {
        mongoMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    private static final MarshallPreProcessor MARSHALL_PRE_PROCESSOR = new MarshallPreProcessor();
    private static final MongoBsonMarshaller MONGO_BSON_MARSHALLER = new JacksonMongoBsonMarshaller(mongoMapper, MARSHALL_PRE_PROCESSOR);
    private static final MongoBsonUnmarshaller MONGO_BSON_UNMARSHALLER = new JacksonMongoBsonUnmarshaller(mongoMapper);

    private static final BsonMarshallingFactory instance = new JacksonMongoBsonMarshallingFactory();

    private JacksonMongoBsonMarshallingFactory() {

    }

    public static BsonMarshallingFactory getInstance() {
        return instance;
    }

    @Override
    public MongoBsonMarshaller createMarshaller() {
        return MONGO_BSON_MARSHALLER;
    }

    @Override
    public MongoBsonUnmarshaller createUnmarshaller() {
        return MONGO_BSON_UNMARSHALLER;
    }

}

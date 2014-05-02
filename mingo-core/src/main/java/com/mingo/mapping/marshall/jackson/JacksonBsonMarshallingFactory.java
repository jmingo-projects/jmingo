package com.mingo.mapping.marshall.jackson;


import com.fasterxml.jackson.databind.SerializationFeature;
import com.mingo.mapping.convert.mongo.type.deserialize.MongoDateDeserializer;
import com.mingo.mapping.convert.mongo.type.deserialize.ObjectIdDeserializer;
import com.mingo.mapping.convert.mongo.type.serialize.BsonDateSerializer;
import com.mingo.mapping.convert.mongo.type.serialize.ObjectIdSerializer;
import com.mingo.mapping.marshall.BsonMarshaller;
import com.mingo.mapping.marshall.BsonMarshallingFactory;
import com.mingo.mapping.marshall.BsonUnmarshaller;
import com.mingo.mapping.marshall.JsonToDBObjectMarshaller;
import com.mingo.mapping.marshall.MarshallPreProcessor;
import org.bson.types.ObjectId;

import java.util.Date;

public class JacksonBsonMarshallingFactory implements BsonMarshallingFactory {
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
    private static final BsonMarshaller MONGO_BSON_MARSHALLER = new JacksonBsonMarshaller(mongoMapper, MARSHALL_PRE_PROCESSOR);
    private static final BsonUnmarshaller MONGO_BSON_UNMARSHALLER = new JacksonBsonUnmarshaller(mongoMapper);

    private static final BsonMarshallingFactory instance = new JacksonBsonMarshallingFactory();

    private JacksonBsonMarshallingFactory() {

    }

    public static BsonMarshallingFactory getInstance() {
        return instance;
    }

    @Override
    public BsonMarshaller createMarshaller() {
        return MONGO_BSON_MARSHALLER;
    }

    @Override
    public BsonUnmarshaller createUnmarshaller() {
        return MONGO_BSON_UNMARSHALLER;
    }

    @Override
    public JsonToDBObjectMarshaller createJsonToDbObjectMarshaller() {
        throw new UnsupportedOperationException("There are no Jackson implementation of JsonToDBObjectMarshaller");
    }

}

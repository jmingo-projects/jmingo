package com.mingo.mapping.converter.custom;

import static com.mingo.mapping.convert.ConversionUtils.getFirstElement;
import com.mingo.mapping.convert.Converter;
import com.mongodb.DBObject;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;

/**
 * Implementation of {@link Converter} interface.
 */
public class BaseMongoConverter<T> implements Converter<T> {

    private MongoConverter mongoConverter;

    /**
     * Constructor with parameters.
     *
     * @param mongoTemplate {@link MongoTemplate}
     */
    public BaseMongoConverter(MongoTemplate mongoTemplate) {
        this.mongoConverter = mongoTemplate.getConverter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T convert(Class<T> type, DBObject source) {
        if (type != null) {
            source = getFirstElement(source);
            return mongoConverter.read(type, source);
        } else {
            throw new IllegalArgumentException(
                MessageFormatter.format("source ( {} ) data cannot be converted to object with {} type", source, type)
                    .getMessage());
        }
    }

}

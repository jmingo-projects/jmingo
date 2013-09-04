package com.mingo.converter;

import com.mingo.convert.Converter;
import com.mingo.convert.DefaultConverter;
import com.mingo.domain.Review;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ReviewCommonConverter extends DefaultConverter<Review> implements Converter<Review> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewCommonConverter.class);

    @Override
    public Review convert(Class<Review> type, DBObject source) {
        return super.convert(type, source);
    }
}

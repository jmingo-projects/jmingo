package com.mingo.convert;

import static com.mingo.convert.ConversionUtils.getFirstElement;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mingo.convert.mongo.type.deserialize.MongoDateDeserializer;
import com.mingo.exceptions.ConversionException;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

/**
 * Copyright 2012-2013 The Mingo Team
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p/>
 * Default implementation of {@link Converter} interface.
 *
 * @param <T> the type of the class modeled by this {@code Class} object.
 */
public class DefaultConverter<T> implements Converter<T> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConverter.class);

    /**
     * Default constructor.
     */
    public DefaultConverter() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Date.class, new MongoDateDeserializer());
        objectMapper.registerModule(module);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public T convert(Class<T> type, DBObject source) {
        T result;
        source = getFirstElement(source);
        String json = JSON.serialize(source);
        try {
            result = objectMapper.readValue(json, type);
        } catch (IOException e) {
            LOGGER.error(ExceptionUtils.getMessage(e));
            throw new ConversionException(e);
        }
        return result;
    }

}

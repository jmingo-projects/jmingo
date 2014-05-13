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
 */
package com.mingo.mongo;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.mingo.config.MongoConfig;
import com.mingo.exceptions.MongoConfigurationException;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static com.mingo.util.PropertyUtils.transform;
import static com.mingo.util.ReflectionUtils.findMethod;

public class MongoDBFactory {

    private String dbName;
    private Mongo mongo;
    private static final Set<String> UNSUPPORTED_OPTIONS = Sets.newHashSet("dbDecoderFactory", "dbEncoderFactory");

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBFactory.class);

    public MongoDBFactory(MongoConfig config) {
        dbName = config.getDbName();
        mongo = create(config);
    }

    public MongoDBFactory(MongoConfig config, Mongo mongo) {
        dbName = config.getDbName();
        this.mongo = mongo;
    }

    public DB getDB() {
        return mongo.getDB(dbName);
    }

    public Mongo getMongo() {
        return mongo;
    }

    private Mongo create(MongoConfig config) {
        Mongo mongo;
        try {
            MongoClientOptions options = createOptions(config);
            mongo = new MongoClient(new ServerAddress(config.getDatabaseHost(), config.getDatabasePort()), options);
            WriteConcern writeConcern = WriteConcern.valueOf(config.getWriteConcern());
            mongo.setWriteConcern(writeConcern);
            LOGGER.debug("set '{}' write concern", writeConcern);
        } catch (UnknownHostException e) {
            throw new MongoConfigurationException(e);
        }
        return mongo;
    }

    /**
     * Creates new instance of {@link MongoClientOptions}.
     * Finds a method in {@link MongoClientOptions.Builder} that matches with a option name from {@link com.mingo.config.MongoConfig#getOptions()}
     * and has same number of arguments and tries invoke satisfied method using reflection with args for appropriate option name.
     *
     * @param config the mongo configuration
     * @return the created {@link MongoClientOptions}
     * @throws MongoConfigurationException
     */
    private MongoClientOptions createOptions(MongoConfig config) throws MongoConfigurationException {
        MongoClientOptions.Builder builder = MongoClientOptions.builder();
        if (MapUtils.isEmpty(config.getOptions())) {
            return builder.build();
        } else {
            for (Map.Entry<String, String> option : config.getOptions().entrySet()) {
                try {
                    if (isUnsupportedOption(option.getKey())) {
                        LOGGER.warn("mongo option: {} cannot be set because isn't supported and should be set directly using MongoClientOptions");
                        continue;
                    }
                    String[] values = StringUtils.split(option.getValue(), ",");
                    Method method = findMethod(MongoClientOptions.Builder.class, (m) ->
                            StringUtils.equalsIgnoreCase(m.getName(), option.getKey())
                                    && (m.getParameterCount() == values.length));

                    if (method != null) {
                        Class<?>[] pTypes = method.getParameterTypes();

                        Object[] args = new Object[values.length];
                        for (int i = 0; i < values.length; i++) {
                            args[i] = getValue(values[i], pTypes[i]);
                        }
                        method.invoke(builder, args);
                    } else {
                        LOGGER.error("no method has been found in MongoClientOptions.Builder with name: '{}' and args: {}",
                                option.getKey(), Arrays.asList(values));
                    }
                } catch (Throwable th) {
                    throw new MongoConfigurationException("failed to set mongo option: " + option.getKey()
                            + " value: " + option.getValue(), th);
                }
            }
        }
        return builder.build();
    }


    private Object getValue(String val, Class<?> type) {
        if (Number.class.isAssignableFrom(type)) {
            Validate.isTrue(NumberUtils.isNumber(val), "value: '%s' isn't correct number", val);
        }
        return transform(type, val);
    }

    private boolean isUnsupportedOption(String name) {
        return Iterables.tryFind(UNSUPPORTED_OPTIONS, input -> StringUtils.equalsIgnoreCase(name, input)).isPresent();
    }

}

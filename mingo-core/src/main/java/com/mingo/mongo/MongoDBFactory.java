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

import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.mingo.config.MongoConfig;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;


/**
 * Created by dmgcodevil on 19.04.2014.
 */
public class MongoDBFactory {

    private String dbName;
    private Mongo mongo;
    private static final Set<String> UNSUPPORTED_OPTIONS = Sets.newHashSet("dbDecoderFactory", "dbEncoderFactory");

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

            MongoClientOptions options = options(config);

            mongo = new MongoClient(new ServerAddress(config.getDatabaseHost(), config.getDatabasePort()), options);

        } catch (UnknownHostException | InvocationTargetException | IllegalAccessException e) {
            throw Throwables.propagate(e);
        }
        return mongo;
    }

    private MongoClientOptions options(MongoConfig config) throws InvocationTargetException, IllegalAccessException {
        MongoClientOptions.Builder builder = MongoClientOptions.builder();
        if (MapUtils.isEmpty(config.getOptions())) {
            return builder.build();
        } else {
            for (Map.Entry<String, String> option : config.getOptions().entrySet()) {
                if (isUnsupportedOption(option.getKey())) {
                    // todo log it
                    continue;
                }
                Method method = getMethodByName(MongoClientOptions.Builder.class, option.getKey());
                if (method != null) {
                    Class<?>[] pTypes = method.getParameterTypes();
                    if (pTypes != null && pTypes.length > 0) {
                        method.invoke(builder, getValue(option.getKey(), option.getValue(), pTypes[0]));
                    }
                }
            }
        }
        return builder.build();
    }

    private Method getMethodByName(Class type, String name) {
        return Iterables.find(Arrays.asList(type.getDeclaredMethods()), (Method method) ->
                StringUtils.equalsIgnoreCase(name, method.getName()));
    }

    // todo change with method
    private Object getValue(String opName, String val, Class<?> pType) {
        if (NumberUtils.isNumber(val)) {
            return NumberUtils.createInteger(val);
        }

        return val; // for String type and other unknown types

    }

    private boolean isUnsupportedOption(String name) {
        return Iterables.tryFind(UNSUPPORTED_OPTIONS, input -> StringUtils.equalsIgnoreCase(name, input)).isPresent();
    }
}

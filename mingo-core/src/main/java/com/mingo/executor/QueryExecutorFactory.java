package com.mingo.executor;

import com.mingo.context.Context;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;

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
public class QueryExecutorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryExecutorFactory.class);

    /**
     * Creates query executor.
     *
     * @param host database host
     * @param port database port
     * @param context mingo context {@link Context}
     * @return query executor {@link QueryExecutor}
     */
    public static QueryExecutor create(String host, int port, Context context) {
        QueryExecutor queryExecutor = null;
        try {
            queryExecutor = create(new Mongo(host, port), context);
        } catch(UnknownHostException e) {
            LOGGER.error(ExceptionUtils.getMessage(e));
        }
        return queryExecutor;
    }

    /**
     * Creates query executor.
     *
     * @param mongo database connection with internal connection pooling {@link Mongo}
     * @param context mingo context {@link Context}
     * @return query executor {@link QueryExecutor}
     */
    public static QueryExecutor create(Mongo mongo, Context context) {
        Validate.notNull(mongo, "mongo cannot be null");
        Validate.notNull(context, "context cannot be null");

        QueryExecutor queryExecutor = null;
        switch(context.getQueryExecutorType()) {
            case MONGO_DRIVER:
                queryExecutor = new MongoQueryExecutor(mongo, context);
                break;
            default:
                queryExecutor = new MongoQueryExecutor(mongo, context);
                break;
        }
        return queryExecutor;
    }

    /**
     * Creates query executor.
     *
     * @param context mingo context {@link Context}
     * @return query executor {@link QueryExecutor}
     */
    public static QueryExecutor create(Context context) {
        QueryExecutor queryExecutor = null;
        Validate.notNull(context, "context cannot be null");
        try {
            queryExecutor = create(new MongoClient(context.getDatabaseHost(),
                context.getDatabasePort()), context);
        } catch(UnknownHostException e) {
            LOGGER.error(ExceptionUtils.getMessage(e));
        }
        return queryExecutor;

    }

}

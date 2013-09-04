package com.mingo.executor;

import com.mingo.query.aggregation.PipelineBuilder;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.springframework.util.Assert;

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
public abstract class AbstractQueryExecutor implements QueryExecutor {

    protected final static int FIRST_ELEMENT = 0;

    private final PipelineBuilder pipelineBuilder = new PipelineBuilder();

    protected PipelineBuilder getPipelineBuilder() {
        return pipelineBuilder;
    }

    /**
     * Perform aggregation query.
     *
     * @param dbCollection db collection
     * @param query        query
     * @return {@link AggregationOutput}
     */
    protected AggregationOutput performAggregationQuery(DBCollection dbCollection, String query) {
        Assert.notNull(dbCollection, "dbCollection cannot be null");
        Assert.hasText(query, "query cannot be null or empty");
        BasicDBList operatorsDB = pipelineBuilder.buildAggregation(query);
        DBObject firstOperator = (DBObject) operatorsDB.remove(FIRST_ELEMENT);
        return dbCollection.aggregate(firstOperator, operatorsDB.toArray(new DBObject[FIRST_ELEMENT]));
    }

}

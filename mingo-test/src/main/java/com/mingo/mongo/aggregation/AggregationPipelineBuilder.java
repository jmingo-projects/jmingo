package com.mingo.mongo.aggregation;

import static com.mingo.mongo.aggregation.AggregationOperators.GROUP;
import static com.mingo.mongo.aggregation.AggregationOperators.MATCH;
import static com.mingo.mongo.aggregation.AggregationOperators.PROJECT;
import static com.mingo.mongo.aggregation.AggregationOperators.UNWIND;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import java.util.List;

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
public final class AggregationPipelineBuilder {

    /* operations to be performed in the aggregation pipeline */
    private List<DBObject> operators = Lists.newArrayList();

    /**
     * Private constructor.
     */
    private AggregationPipelineBuilder() {
    }

    /**
     * Creates an empty object.
     *
     * @return {@link AggregationPipelineBuilder}
     */
    public static AggregationPipelineBuilder start() {
        return new AggregationPipelineBuilder();
    }

    /**
     * Use project to quickly select the fields that you want to include or
     * exclude from the response. Consider the following aggregation framework
     * operation.
     *
     * @param field a key-value map that can be saved to the database
     * @return {@link AggregationPipelineBuilder}
     */
    public AggregationPipelineBuilder project(DBObject field) {
        return buildOperator(PROJECT, field);
    }

    /**
     * Drops documents that do not match the condition from the aggregation pipeline,
     * and it passes documents that match along the pipeline unaltered.
     *
     * @param field a key-value map that can be saved to the database
     * @return {@link AggregationPipelineBuilder}
     */
    public AggregationPipelineBuilder match(DBObject field) {
        return buildOperator(MATCH, field);
    }

    /**
     * Groups documents together for the purpose of calculating aggregate
     * values based on a collection of documents.
     *
     * @param field a key-value map that can be saved to the database
     * @return {@link AggregationPipelineBuilder}
     */
    public AggregationPipelineBuilder group(DBObject field) {
        return buildOperator(GROUP, field);
    }

    /**
     * Add unwind operator.
     *
     * @param field field
     * @return {@link AggregationPipelineBuilder}
     */
    public AggregationPipelineBuilder unwind(String field) {
        operators.add(BasicDBObjectBuilder.start(UNWIND.getMongoName(), field).get());
        return this;
    }

    /**
     * Add operator to pipeline.
     *
     * @param field field
     * @return {@link AggregationPipelineBuilder}
     */
    public AggregationPipelineBuilder add(DBObject field) {
        if (field != null && !operators.contains(field)) {
            operators.add(field);
        }
        return this;
    }

    /**
     * Complete query building and return list of operators.
     *
     * @return list of operators
     */
    public List<DBObject> build() {
        return operators;
    }

    /**
     * Gets pipeline.
     *
     * @return pipeline
     */
    public String asString() {
        return operators.toString();
    }

    /**
     * Builds operator.
     *
     * @param aggregationOperator {@link AggregationOperators}
     * @param field               a key-value map that can be saved to the database
     * @return {@link AggregationPipelineBuilder}
     */
    private AggregationPipelineBuilder buildOperator(AggregationOperators aggregationOperator, DBObject field) {
        operators.add(BasicDBObjectBuilder.start(aggregationOperator.getMongoName(), field).get());
        return this;
    }

}

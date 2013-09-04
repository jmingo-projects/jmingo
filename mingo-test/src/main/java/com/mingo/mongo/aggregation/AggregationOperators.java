package com.mingo.mongo.aggregation;

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
public enum AggregationOperators {
    ALL("$all"),
    ADD("$add"),
    AVG("$avg"),
    COND("$cond"),
    EXISTS("$exists"),
    EQ("$eq"),
    GT("$gt"),
    LT("$lt"),
    IFNULL("$ifNull"),
    GROUP("$group"),
    LIMIT("$limit"),
    MATCH("$match"),
    PROJECT("$project"),
    PUSH("$push"),
    SKIP("$skip"),
    SORT("$sort"),
    SUM("$sum"),
    UNWIND("$unwind"),
    ELEM_MATCH("$elemMatch"),
    FIRST("$first"),
    IN("$in"),
    ADD_TO_SET("$addToSet"),
    REGEX("$regex"),
    OPTIONS("$options");

    private final String mongoName;

    /**
     * default constructor with argument.
     *
     * @param nameIn name of Mongo's operator
     */
    AggregationOperators(String nameIn) {
        mongoName = nameIn;
    }

    /**
     * Get name of Mongo's operator.
     *
     * @return name of Mongo's operator
     */
    public String getMongoName() {
        return mongoName;
    }
}

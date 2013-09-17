package com.mingo.query.parser;

import static com.mingo.query.parser.json.JsonParserType.DEFAULT;
import static com.mingo.query.parser.json.JsonParserType.ESCAPE;
import static com.mingo.query.util.QueryUtils.wrap;
import com.mingo.query.QueryStatement;
import com.mingo.query.QueryType;
import com.mingo.query.parser.json.EscapeParser;
import com.mingo.query.parser.json.JsonParser;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import org.apache.commons.lang3.Validate;

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
public class QueryParser {

    private static final String DEFAULT_QUERY = "{$match : {}}";

    /**
     * Parses a JSON string representing a JSON value.
     *
     * @param queryStatement query statement
     * @return the object
     */
    public DBObject parse(QueryStatement queryStatement) {
        Validate.notNull(queryStatement, "queryStatement cannot be null");
        QueryType queryType = queryStatement.getQueryType();
        JsonParser jsonParser = getJsonParser(queryStatement.isEscapeNullParameters());
        return QueryType.SIMPLE.equals(queryType) ?
            parse(queryStatement.getPreparedQuery(), jsonParser) :
            parseAggregation(queryStatement, jsonParser);
    }


    private DBObject parse(String json, JsonParser jsonParser) {
        return jsonParser.parse(json);
    }

    // if escape
    private BasicDBList parseAggregation(QueryStatement queryStatement, JsonParser jsonParser) {
        BasicDBList operators = (BasicDBList) jsonParser.parse(wrap(queryStatement.getPreparedQuery()));
        if (operators.isEmpty() && jsonParser instanceof EscapeParser) {
            return (BasicDBList) DEFAULT.getJsonParser().parse(wrap(DEFAULT_QUERY));
        }
        return operators;
    }

    private JsonParser getJsonParser(boolean escapeNullParameters) {
        return escapeNullParameters ? ESCAPE.getJsonParser() : DEFAULT.getJsonParser();
    }

}

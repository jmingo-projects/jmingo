package com.mingo.query.fragment;

import static com.mingo.mongo.util.MongoUtil.toDBObject;
import static com.mingo.query.util.QueryUtils.wrap;
import com.google.common.collect.Lists;
import com.mingo.query.Query;
import com.mingo.query.QueryAnalyzerType;
import com.mingo.query.QueryType;
import com.mingo.query.analyzer.QueryAnalyzer;
import com.mingo.query.analyzer.QueryAnalyzerFactory;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
public class QueryFragmentInjector {

    private QueryAnalyzer queryAnalyzer = QueryAnalyzerFactory.getQueryAnalyzer(QueryAnalyzerType.JEXL);

    private static final String FRAGMENT = "fragment";

    /**
     * Constructor with parameters.
     *
     * @param queryAnalyzer query analyzer
     */
    public QueryFragmentInjector(QueryAnalyzer queryAnalyzer) {
        this.queryAnalyzer = queryAnalyzer;
    }

    /**
     * Default constructor.
     */
    public QueryFragmentInjector() {
    }

    /**
     * Inject fragment body in query if fragment satisfies the conditions.
     *
     * @param query      query
     * @param parameters parameters
     * @return processed query
     */
    public String injectFragments(Query query, Map<String, Object> parameters) {
        String result;
        if (QueryType.AGGREGATION.equals(query.getQueryType())) {
            BasicDBList basicDBList = (BasicDBList) JSON.parse(wrap(query.getBody()));
            result = injectFragmentsToAggregationQuery(basicDBList, parameters);
        } else {
            result = query.getBody();
        }
        return result;
    }

    private String injectFragmentsToAggregationQuery(BasicDBList query, Map<String, Object> parameters) {
        List<String> operators = Lists.newArrayList();
        Iterator<Object> i = query.iterator();
        while (i.hasNext()) {
            DBObject dbObject = toDBObject(i.next());
            if (dbObject.containsField(FRAGMENT)) {
                BasicDBList fragmentDB = toDBObject(dbObject.get(FRAGMENT));
                i.remove();
                String fBody = processFragment(fragmentDB, parameters);
                if (StringUtils.isNotEmpty(fBody)) {
                    operators.add(fBody);
                }
            } else {
                operators.add(dbObject.toString());
            }
        }
        return StringUtils.join(operators, ",");
    }

    private String processFragment(BasicDBList fragmentDB, Map<String, Object> parameters) {
        String body = null;
        String condition = ((DBObject) fragmentDB.get(0)).get("condition").toString();
        if (queryAnalyzer.evaluate(condition, parameters)) {
            body = ((DBObject) fragmentDB.get(1)).get("body").toString();
        }
        return body;
    }

}

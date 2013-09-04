package com.mingo.query.analyzer;

import com.google.common.collect.Maps;
import com.mingo.query.QueryAnalyzerType;

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
public class QueryAnalyzerFactory {

    private static Map<QueryAnalyzerType, QueryAnalyzer> queryAnalyzerMap = Maps.newHashMap();

    private QueryAnalyzerFactory() {

    }

    /**
     * Gets query analyzer for specified type.
     *
     * @param queryAnalyzerType query analyzer type
     * @return implementation of {@link QueryAnalyzer} interface
     */
    public static synchronized QueryAnalyzer getQueryAnalyzer(QueryAnalyzerType queryAnalyzerType) {
        QueryAnalyzer queryAnalyzer = queryAnalyzerMap.get(queryAnalyzerType);
        if (queryAnalyzer == null) {
            queryAnalyzer = createQueryAnalyzer(queryAnalyzerType);
            queryAnalyzerMap.put(queryAnalyzerType, queryAnalyzer);
        }
        return queryAnalyzer;
    }

    /**
     * Creates query analyzer for specified type.
     *
     * @param queryAnalyzerType query analyzer type
     * @return implementation of {@link QueryAnalyzer} interface
     */
    public static QueryAnalyzer createQueryAnalyzer(QueryAnalyzerType queryAnalyzerType) {
        QueryAnalyzer queryAnalyzer;
        switch (queryAnalyzerType) {
            case JEXL:
                queryAnalyzer = new JEXLQueryAnalyzer();
                break;
            case SPRING_EL:
                queryAnalyzer = new SpringELQueryAnalyzer();
                break;
            default:
                queryAnalyzer = new JEXLQueryAnalyzer();
        }
        return queryAnalyzer;
    }
}

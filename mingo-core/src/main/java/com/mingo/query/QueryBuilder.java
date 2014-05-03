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
package com.mingo.query;


import com.mingo.query.el.ELEngine;
import com.mingo.query.el.ELEngineFactory;
import com.mingo.util.StringUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.mingo.query.QueryType.PLAIN;
import static com.mingo.util.QueryUtils.pipeline;

//todo should be thread safe
public class QueryBuilder implements QBuilder {

    private StringBuilder query = new StringBuilder();
    private ELEngine elEngine = ELEngineFactory.create(ELEngineType.SPRING_EL);
    private Map<String, Object> parameters = Collections.emptyMap();
    private QueryType queryType;

    public QueryBuilder(QueryType queryType, Map<String, Object> parameters) {
        this.queryType = queryType;
        this.parameters = parameters;
    }

    public QueryBuilder(QueryType queryType, ELEngine elEngine, Map<String, Object> parameters) {
        this.queryType = queryType;
        this.elEngine = elEngine;
        this.parameters = parameters;
    }

    @Override
    public boolean append(TextElement queryEl) {
        query.append(queryEl.getText());
        return true;
    }

    @Override
    public boolean append(ConditionElement conditionEl) {
        boolean appended = false;
        if(elEngine.evaluate(conditionEl.getExpression(), parameters)) {
            query.append(conditionEl.getText());
            appended = true;
        }
        return appended;
    }

    @Override
    public boolean appendIfAbsent(String str) {
        String dump = query.toString();
        StringUtils.appendIfAbsent(query, str);
        return org.apache.commons.lang3.StringUtils.equals(dump, query.toString());
    }

    @Override
    public String buildQuery() {
        String prepared = StringUtils.replaceLastComma(query.toString());
        return wrap(queryType, prepared);
    }

    public static String getFullQueryText(QueryType qType, List<QueryElement> queryElements) {
        StringBuilder builder = new StringBuilder();
        queryElements.forEach(el -> {
            String elText = el.asString();
            builder.append(StringUtils.appendIfAbsent(elText, ","));
        });
        String text = StringUtils.replaceLastComma(builder.toString());
        return wrap(qType, text);
    }

    private static String wrap(QueryType qType, String str) {
        if(PLAIN.equals(qType)) {
            StringBuilder builder = new StringBuilder();
            str = org.apache.commons.lang3.StringUtils.trim(str);
            if(!str.startsWith("{")) {
                builder.append("{");
            }
            builder.append(str);
            if(!str.endsWith("}")) {
                builder.append("}");
            }
            return builder.toString();
        }
        return pipeline(str);
    }

}

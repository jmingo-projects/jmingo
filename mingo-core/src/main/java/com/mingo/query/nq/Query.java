package com.mingo.query.nq;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Map;


public class Query {

    private List<QueryElement> queryElements = Lists.newArrayList();

    public Query add(QueryElement queryEl) {
        queryElements.add(queryEl);
        return this;
    }

    public Query add(List<QueryElement> qElements) {
        queryElements.addAll(qElements);
        return this;
    }

    public List<QueryElement> getQueryElements() {
        return ImmutableList.copyOf(queryElements);
    }

    public String build() {
        return build(Collections.emptyMap());
    }

    public String build(Map<String, Object> parameters) {
        QBuilder queryBuilder = new QueryBuilder(parameters);
        queryElements.forEach(element -> element.accept(queryBuilder));
        return queryBuilder.buildQuery();
    }
}

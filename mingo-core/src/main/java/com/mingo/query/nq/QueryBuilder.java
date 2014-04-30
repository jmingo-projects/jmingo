package com.mingo.query.nq;


import com.mingo.query.analyzer.SpringELQueryAnalyzer;

import java.util.Collections;
import java.util.Map;

public class QueryBuilder implements QBuilder {

    private StringBuilder query = new StringBuilder();
    private SpringELQueryAnalyzer elEngine = new SpringELQueryAnalyzer(); //todo make as property
    private Map<String, Object> parameters = Collections.emptyMap();

    public QueryBuilder() {
    }

    public QueryBuilder(Map<String, Object> parameters) {
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
        if (elEngine.evaluate(conditionEl.getExpression(), parameters)) {
            query.append(conditionEl.getText());
            appended = true;
        }
        return appended;
    }

    @Override
    public String buildQuery() {
        return query.toString();
    }

}

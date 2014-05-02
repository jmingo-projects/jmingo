package com.mingo.query;


import com.mingo.query.el.ELEngine;
import com.mingo.query.el.ELEngineFactory;

import java.util.Collections;
import java.util.Map;

public class QueryBuilder implements QBuilder {

    private StringBuilder query = new StringBuilder();
    private ELEngine elEngine = ELEngineFactory.create(ELEngineType.SPRING_EL);
    private Map<String, Object> parameters = Collections.emptyMap();

    public QueryBuilder() {
    }

    public QueryBuilder(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public QueryBuilder(ELEngine elEngine, Map<String, Object> parameters) {
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

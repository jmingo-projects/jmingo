package com.mingo.query.el;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.lang3.Validate;

import java.util.Map;


public class JexlELEngine implements ELEngine {

    private final JexlEngine jexl = new JexlEngine();

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized boolean evaluate(String expression, Map<String, Object> parameters) {
        Validate.notBlank(expression, "expression cannot be empty");
        Expression e = jexl.createExpression(expression);
        JexlContext jc = new MapContext();
        if (MapUtils.isNotEmpty(parameters)) {
            for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
                jc.set(parameter.getKey(), parameter.getValue());
            }
        }
        Object result = e.evaluate(jc);
        if (result instanceof Boolean) {
            return (Boolean) result;
        } else {
            throw new RuntimeException("expected boolean result of evaluation: " +
                    expression + ", but the result is: " + result);
        }
    }
}

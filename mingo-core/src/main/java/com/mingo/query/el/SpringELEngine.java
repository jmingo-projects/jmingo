package com.mingo.query.el;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;


public class SpringELEngine implements ELEngine {

    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized boolean evaluate(String expression, Map<String, Object> parameters) {
        Validate.notBlank(expression, "expression cannot be empty");
        Expression exp = parser.parseExpression(expression);
        EvaluationContext context = new StandardEvaluationContext();
        if (MapUtils.isNotEmpty(parameters)) {
            for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
                context.setVariable(parameter.getKey(), parameter.getValue());
            }
        }
        return exp.getValue(context, Boolean.class);
    }

}

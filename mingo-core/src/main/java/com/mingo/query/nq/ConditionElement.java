package com.mingo.query.nq;

/**
 * Immutable.
 */
public class ConditionElement extends TextElement {

    private String expression = "";

    public ConditionElement() {
    }

    public ConditionElement(String body, String expression) {
        super(body);
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }
}

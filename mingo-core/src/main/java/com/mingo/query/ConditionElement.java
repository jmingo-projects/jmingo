package com.mingo.query;

/**
 * Immutable.
 */
public class ConditionElement extends TextElement {

    private final String expression;

    public ConditionElement(String body) {
        super(body);
        this.expression = "";
    }

    public ConditionElement(String body, String expression) {
        super(body);
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }
}

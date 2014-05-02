package com.mingo.query;

/**
 * Immutable.
 */
public class TextElement implements QueryElement {

    private final String text;

    public TextElement(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public void accept(QBuilder queryBuilder) {
        queryBuilder.append(this);
    }

    @Override
    public String asString() {
        return text;
    }

}

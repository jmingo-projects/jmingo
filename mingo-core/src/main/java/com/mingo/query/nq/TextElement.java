package com.mingo.query.nq;

/**
 * Immutable.
 */
public class TextElement implements QueryElement {

    private String text;

    public TextElement() {
    }

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
}

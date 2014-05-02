package com.mingo.query;

public interface QueryElement {

    void accept(QBuilder visitor);

    String asString();
}

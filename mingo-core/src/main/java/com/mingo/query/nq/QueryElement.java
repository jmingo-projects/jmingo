package com.mingo.query.nq;


public interface QueryElement {
    void accept(QBuilder visitor);
}

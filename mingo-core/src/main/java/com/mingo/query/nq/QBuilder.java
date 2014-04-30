package com.mingo.query.nq;


public interface QBuilder {

    boolean append(TextElement queryEl);
    boolean append(ConditionElement queryEl);
    String buildQuery();
}

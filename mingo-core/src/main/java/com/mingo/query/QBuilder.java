package com.mingo.query;


public interface QBuilder {

    boolean append(TextElement queryEl);
    boolean append(ConditionElement queryEl);
    String buildQuery();
}

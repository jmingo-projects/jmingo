package com.mingo.query;


public interface QBuilder {

    boolean append(TextElement queryEl);

    boolean append(ConditionElement queryEl);

    /**
     * Append the suffix to the query if query doesn't end with the specified suffix.
     */
    boolean appendIfAbsent(String suffix);

    String buildQuery();
}

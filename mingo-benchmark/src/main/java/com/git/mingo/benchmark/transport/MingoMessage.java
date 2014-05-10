package com.git.mingo.benchmark.transport;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;


public class MingoMessage<T> implements Serializable {
    private final OperationType operationType;
    private final T body;

    public MingoMessage(OperationType operationType, T body) {
        this.operationType = operationType;
        this.body = body;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public T getBody() {
        return body;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("operationType", operationType)
                .append("body", body)
                .toString();
    }
}

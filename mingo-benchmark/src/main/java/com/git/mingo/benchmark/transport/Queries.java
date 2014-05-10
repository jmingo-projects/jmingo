package com.git.mingo.benchmark.transport;

import java.io.Serializable;
import java.util.Set;

public class Queries extends MingoMessage<Set<String>> implements Serializable {

    public Queries(Set<String> body) {
        super(OperationType.UNKNOWN, body);
    }
}

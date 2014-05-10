package com.git.mingo.benchmark.transport;

public class TextMessage extends MingoMessage<String> {


    public TextMessage(OperationType operationType) {
        super(operationType, "");
    }

    public TextMessage(OperationType operationType, String body) {
        super(operationType, body);
    }

}

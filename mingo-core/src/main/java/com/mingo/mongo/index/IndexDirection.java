package com.mingo.mongo.index;


public enum IndexDirection {
    ASC(1),
    DESC(-1);

    private int val;

    IndexDirection(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }
}

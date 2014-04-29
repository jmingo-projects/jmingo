package com.mingo.query.replacer;

public interface ReplacementCallback<T> {

    void doReplace(T item);
}

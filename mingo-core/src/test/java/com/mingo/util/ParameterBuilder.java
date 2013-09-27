package com.mingo.util;

import com.google.common.collect.Maps;

import java.util.Map;

public class ParameterBuilder {
    private Map<String, Object> parameters = Maps.newHashMap();

    public ParameterBuilder add(String key, Object val) {
        parameters.put(key, val);
        return this;
    }

    public Map<String, Object> build() {
        return parameters;
    }
}
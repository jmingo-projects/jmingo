package com.mingo.config;


public class MingoContextConfig {

    private boolean benchmarkEnabled = false;

    public boolean isBenchmarkEnabled() {
        return benchmarkEnabled;
    }

    public void setBenchmarkEnabled(boolean benchmarkEnabled) {
        this.benchmarkEnabled = benchmarkEnabled;
    }
}

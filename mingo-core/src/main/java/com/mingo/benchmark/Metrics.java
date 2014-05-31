/**
 * Copyright 2012-2013 The Mingo Team
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mingo.benchmark;


import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Represents information about profiling.
 * This class is immutable and can be shared between threads without any synchronization.
 */
public class Metrics implements Serializable {
    /**
     * name of metrics, for example name of method or query name.
     */
    private final String name;
    /**
     * profiling start time.
     */
    private final long startTime;
    /**
     * execution time.
     */
    private final long executionTime;

    /**
     * Represents time durations at a given unit of
     * granularity.
     */
    private final TimeUnit timeUnit;

    public Metrics(Builder builder) {
        this.name = builder.name;
        this.startTime = builder.startTime;
        this.executionTime = builder.executionTime;
        this.timeUnit = builder.timeUnit;
    }

    public String getName() {
        return name;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    /**
     * Simple builder for {@link Metrics}.
     */
    public static class Builder {
        private String name = "";
        private long startTime = 0L;
        private long executionTime = 0L;
        private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

        public Builder name(String pName) {
            this.name = pName;
            return this;
        }

        public Builder startTime(long pStartTime) {
            this.startTime = pStartTime;
            return this;
        }

        public Builder executionTime(long pExecutionTime) {
            this.executionTime = pExecutionTime;
            return this;
        }

        public Builder timeUnit(TimeUnit pTimeUnit) {
            this.timeUnit = pTimeUnit;
            return this;
        }

        public Metrics build() {
            return new Metrics(this);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("startTime", startTime)
                .append("executionTime", executionTime)
                .append("timeUnit", timeUnit)
                .toString();
    }
}


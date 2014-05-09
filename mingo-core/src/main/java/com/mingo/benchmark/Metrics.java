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

import java.util.concurrent.TimeUnit;

public class Metrics {
    private final String name;
    private final long startTime;
    private final long executionTime;
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

    public static class Builder {
        private String name = "";
        private long startTime = 0L;
        private long executionTime = 0L;
        private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder startTime(long startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder executionTime(long executionTime) {
            this.executionTime = executionTime;
            return this;
        }

        public Builder timeUnit(TimeUnit timeUnit) {
            this.timeUnit = timeUnit;
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


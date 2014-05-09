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


import java.util.concurrent.TimeUnit;

public class QueryProfiler implements Profiler {

    private final String queryId;
    private long startTime;
    private MetricsTransfer metricsTransfer;

    public QueryProfiler(String queryId, MetricsTransfer metricsTransfer) {
        this.queryId = queryId;
        this.metricsTransfer = metricsTransfer;
    }

    @Override
    public Profiler start() {
        this.startTime = System.nanoTime();
        return this;
    }

    @Override
    public void stop() {
        long executionTime = System.nanoTime() - startTime;
        Metrics metrics = new Metrics.Builder()
                .name(queryId)
                .timeUnit(TimeUnit.NANOSECONDS)
                .startTime(startTime)
                .executionTime(executionTime).build();
        profile(metrics);
    }

    private void profile(Metrics metrics) {
        metricsTransfer.sendEvent(new MetricsEvent(metrics));
    }

}

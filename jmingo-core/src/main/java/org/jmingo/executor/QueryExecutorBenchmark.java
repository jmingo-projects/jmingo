/**
 * Copyright 2013-2014 The JMingo Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jmingo.executor;


import org.jmingo.benchmark.Profiler;
import org.jmingo.benchmark.Profilers;

import java.util.List;
import java.util.Map;

/**
 * Decorator for query executor to measure queries execution time.
 */
public class QueryExecutorBenchmark implements QueryExecutor {

    private QueryExecutor queryExecutor;

    /**
     * Constructor with parameters.
     *
     * @param queryExecutor the query executor
     */
    public QueryExecutorBenchmark(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T queryForObject(String queryName, Class<T> type, Map<String, Object> parameters) {
        Profiler profiler = Profilers.newQueryProfiler(queryName).start();
        T result = queryExecutor.queryForObject(queryName, type, parameters);
        profiler.stop();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T queryForObject(String queryName, Class<T> type) {
        Profiler profiler = Profilers.newQueryProfiler(queryName).start();
        T result = queryExecutor.queryForObject(queryName, type);
        profiler.stop();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<T> queryForList(String queryName, Class<T> type, Map<String, Object> parameters) {
        Profiler profiler = Profilers.newQueryProfiler(queryName).start();
        List<T> result = queryExecutor.queryForList(queryName, type, parameters);
        profiler.stop();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<T> queryForList(String queryName, Class<T> type) {
        Profiler profiler = Profilers.newQueryProfiler(queryName).start();
        List<T> result = queryExecutor.queryForList(queryName, type);
        profiler.stop();
        return result;
    }

}

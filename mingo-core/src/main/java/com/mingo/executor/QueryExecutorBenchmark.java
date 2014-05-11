package com.mingo.executor;


import com.mingo.benchmark.Profiler;
import com.mingo.benchmark.Profilers;

import java.util.List;
import java.util.Map;

public class QueryExecutorBenchmark implements QueryExecutor {

    private QueryExecutor queryExecutor;

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

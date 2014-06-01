package com.mingo.demo.benchmark;

import com.mingo.benchmark.BenchmarkService;
import com.mingo.benchmark.Metrics;
import com.mingo.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("benchmarkLoggerService")
public class BenchmarkLoggerService implements BenchmarkService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BenchmarkLoggerService.class);

    @Override
    public void init(Context context) {
    }

    @Override
    public void submit(Metrics metric) {
        LOGGER.debug(metric.toString());
    }

    @Override
    public void destroy() {
    }
}

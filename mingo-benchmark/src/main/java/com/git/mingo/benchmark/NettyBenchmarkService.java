package com.git.mingo.benchmark;

import com.git.mingo.benchmark.transport.Queries;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mingo.benchmark.BenchmarkService;
import com.mingo.benchmark.Metrics;
import com.mingo.context.Context;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

@Component("nettyBenchmarkService")
public class NettyBenchmarkService implements BenchmarkService {

    private ProducerTemplate producerTemplate;
    private Map<String, List<Metrics>> metricsMap = new ConcurrentHashMap<>(new WeakHashMap<>());
    private Context context;
    private Set<String> quiresIds;

    @Autowired
    public NettyBenchmarkService(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyBenchmarkService.class);

    @Override
    public void init(Context context) {
        this.context = context;
        this.quiresIds = Sets.newHashSet(context.getQueryManager().getQueries().keySet());
    }

    @Override
    public void submit(Metrics metric) {
        metricsMap.computeIfAbsent(metric.getName(), (s) -> Lists.newArrayList());
        metricsMap.computeIfPresent(metric.getName(), (s, val) -> {
            val.add(metric);
            return val;
        });
    }

    public Queries getQueriesNames() {
        LOGGER.debug("getQueriesNames()");
        return new Queries(quiresIds);
    }

    public List<Metrics> getMetrics(String queryName) {
        LOGGER.debug("getMetrics( queryName={} )", queryName);
        List<Metrics> mtr = metricsMap.remove(queryName);
        if (mtr == null) {
            mtr = Collections.emptyList();
        }
        return mtr;
    }

    @Override
    public void destroy() {
    }
}

package com.git.mingo.benchmark.client;

import com.git.mingo.benchmark.transport.OperationType;
import com.git.mingo.benchmark.transport.Queries;
import com.git.mingo.benchmark.transport.TextMessage;
import com.mingo.benchmark.Metrics;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class BenchmarkClient {

    private ProducerTemplate producerTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(BenchmarkClient.class);

    @Autowired
    public void setProducerTemplate(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    public Set<String> getQueriesNames() {
        TextMessage message = new TextMessage(OperationType.GET_QUERIES);
        Object object = producerTemplate.sendBody("direct:getQueriesNames", ExchangePattern.InOut, message);
        LOGGER.debug("queries received: {}", object);
        Queries queries = (Queries) object;
        return queries.getBody();
    }

    public List<Metrics> getMetrics(String queryName) {
        TextMessage message = new TextMessage(OperationType.GET_METRICS, queryName);
        Object object = producerTemplate.sendBody("direct:getMetrics", ExchangePattern.InOut, message);
        LOGGER.debug("metrics received: {}", object);
        return (List<Metrics>) object;
    }

}

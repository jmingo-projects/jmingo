package com.git.mingo.benchmark.camel.route.server;

import com.git.mingo.benchmark.transport.MingoMessage;
import com.git.mingo.benchmark.transport.TextMessage;
import com.git.mingo.benchmark.transport.OperationType;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.spring.SpringRouteBuilder;


public class ServerRouteBuilder extends SpringRouteBuilder {

    public static final String NETTY_BENCHMARK_SERVICE = "nettyBenchmarkService";

    @Override
    public void configure() throws Exception {

        // fake route to avoid exception with auto startup for netty route
        from("direct:start").to("mock:result");


        from("netty:tcp://localhost:5540") //todo make it configurable
                .choice()
                .when(exchange ->
                        eventTypeMatches(OperationType.GET_QUERIES, exchange))
                .beanRef(NETTY_BENCHMARK_SERVICE, "getQueriesNames")
                .when(exchange ->
                        eventTypeMatches(OperationType.GET_METRICS, exchange)).process(new Processor() {
            public void process(Exchange exchange) {
                Message in = exchange.getIn();
                MingoMessage message = (MingoMessage) in.getBody();
                in.setBody(message.getBody());
            }
        }).beanRef(NETTY_BENCHMARK_SERVICE, "getMetrics");
        // todo otherwise to custom dead chanel

    }

    private boolean eventTypeMatches(OperationType operationType, Exchange exchange) {
        return operationType.equals(getMessage(exchange).getOperationType());
    }

    private TextMessage getMessage(Exchange exchange) {
        Object obj = exchange.getIn().getBody();
        if (obj instanceof TextMessage) {
            return (TextMessage) obj;
        } else {
            throw new RuntimeException("unsupported type, expected: " + TextMessage.class.getName());
        }
    }
}

package com.git.mingo.benchmark.camel.route.client;

import org.apache.camel.spring.SpringRouteBuilder;


public class ClientRouteBuilder extends SpringRouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:getQueriesNames")
                .to("netty:tcp://localhost:5540"); //todo make it configurable

        from("direct:getMetrics")
                .to("netty:tcp://localhost:5540"); //todo make it configurable
    }
}

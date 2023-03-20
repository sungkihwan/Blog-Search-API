package com.daum.common.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

public class LoggingFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

    public static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            LOGGER.info("Request: {} {}", request.method(), request.url());
            request.headers().forEach((name, values) -> values.forEach(value -> LOGGER.info("{}: {}", name, value)));
            return Mono.just(request);
        });
    }

    public static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            LOGGER.info("Response: {}", response.statusCode());
            return Mono.just(response);
        });
    }
}


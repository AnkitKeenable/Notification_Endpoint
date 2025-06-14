package com.example.util;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class LogMasker implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(LogMasker.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        Object body = exchange.getIn().getBody();

        if (body instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> mapBody = (Map<String, Object>) body;

            if (mapBody.containsKey("auth")) {
                Map<String, Object> auth = (Map<String, Object>) mapBody.get("auth");

                if (LOG.isDebugEnabled()) {
                    // DEBUG level shows actual values
                    LOG.debug("Auth details (DEBUG): {}", auth);
                } else {
                    // INFO level shows masked values
                    LOG.info("Auth details (INFO): {}", maskAuthData(auth));
                }
            }
        }
    }

    private Map<String, Object> maskAuthData(Map<String, Object> auth) {
        Map<String, Object> masked = new HashMap<>();
        auth.forEach((key, value) -> {
            if (value instanceof String) {
                masked.put(key, "*****");
            } else {
                masked.put(key, value);
            }
        });
        return masked;
    }
}
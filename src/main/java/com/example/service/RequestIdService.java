package com.example.service;


import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class RequestIdService {
    private final AtomicLong counter = new AtomicLong(0);
    private final String prefix = String.valueOf(Instant.now().toEpochMilli());

    public String generateId() {
        return prefix + counter.incrementAndGet();
    }
}

package com.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class RequestCounterService {
    private final AtomicLong counter = new AtomicLong(0);

    public long getNextId() {
        return counter.incrementAndGet();
    }

    public long getCurrentId() {
        return counter.get();
    }
}
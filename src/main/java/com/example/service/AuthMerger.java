package com.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
@Named("authMerger") // Explicitly set the bean name
public class AuthMerger {
    @ConfigProperty(name = "auth.appId")
    String appId;

    @ConfigProperty(name = "auth.accountId")
    String accountId;

    @ConfigProperty(name = "auth.apiKey")
    String apiKey;

    public Map<String, Object> merge(Map<String, Object> input) {
        Map<String, Object> request = new HashMap<>();
        request.put("filter", input.get("filter"));

        Map<String, Object> auth = new HashMap<>();
        auth.put("appId", appId);
        auth.put("accountId", accountId);
        auth.put("apiKey", apiKey);

        request.put("auth", auth);
        return request;
    }
}

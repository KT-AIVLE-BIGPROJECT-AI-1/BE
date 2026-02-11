package com.aivle.big_project.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ai")
public record AiProperties (
        String baseUrl,
        int connectTimeoutMs,
        int responseTimeoutMs
) {}

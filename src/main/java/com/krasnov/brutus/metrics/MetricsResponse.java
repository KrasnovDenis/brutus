package com.krasnov.brutus.metrics;

import lombok.Data;

import java.util.Map;

@Data
public class MetricsResponse {
    private Map<String, ?> metrics;
    private String application;
}

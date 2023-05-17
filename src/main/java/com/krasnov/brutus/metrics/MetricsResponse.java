package com.krasnov.brutus.metrics;

import lombok.Data;

import java.util.Map;

@Data
public class MetricsResponse {
    private Map<String, Object> metrics;
    private String application;
}

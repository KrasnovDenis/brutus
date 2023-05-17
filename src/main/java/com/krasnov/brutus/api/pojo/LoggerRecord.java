package com.krasnov.brutus.api.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class LoggerRecord {
    private String log;
    private String podName;
    private String namespace;
    private String requestId;
    private Map<String, String> timestamp;
}

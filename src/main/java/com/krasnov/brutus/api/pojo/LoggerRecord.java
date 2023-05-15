package com.krasnov.brutus.api.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoggerRecord {
    private String log;
    private String podName;
    private String namespace;
    private String requestId;
    private String timestamp;
}

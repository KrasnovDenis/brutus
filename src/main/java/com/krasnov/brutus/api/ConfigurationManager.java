package com.krasnov.brutus.api;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
public class ConfigurationManager {

    private Set<LoggerEntity> config;

    public ConfigurationManager(Set<LoggerEntity> config) {
        this.config = new LinkedHashSet<>(config);
    }

    @Data
    public static class LoggerEntity {
        private Set<String> pods;
        private String namespace;
    }
}

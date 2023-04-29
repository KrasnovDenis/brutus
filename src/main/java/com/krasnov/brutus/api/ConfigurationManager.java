package com.krasnov.brutus.api;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Component
public class ConfigurationManager {

    private Set<LoggerEntity> config;

    public ConfigurationManager() {
        this(new LinkedHashSet<>());
    }

    public ConfigurationManager(Set<LoggerEntity> config) {
        this.config = config;
    }

    @Data
    public static class LoggerEntity {
        private Set<String> pods;
        private String namespace;
    }

    public void overwriteConfig(Set<LoggerEntity> newConfig) {
        this.config = newConfig;
    }

    public Set<LoggerEntity> getRepresentation() {
        return config;
    }
}

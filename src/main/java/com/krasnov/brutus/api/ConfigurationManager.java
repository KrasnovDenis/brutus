package com.krasnov.brutus.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
public class ConfigurationManager {

    private List<LoggerEntity> config;

    public ConfigurationManager() {
        this(new ArrayList<>());
    }

    public ConfigurationManager(List<LoggerEntity> config) {
        this.config = config;
    }

    @Data
    public static class LoggerEntity {
        private List<String> pods;
        private String namespace;
    }

    public void overwriteConfig(List<LoggerEntity> newConfig) {
        this.config = newConfig;
    }

    public List<LoggerEntity> getRepresentation() {
        return config;
    }
}

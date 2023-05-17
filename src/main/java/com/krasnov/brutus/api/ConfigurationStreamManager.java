package com.krasnov.brutus.api;

import com.krasnov.brutus.metrics.MetricsManager;
import com.krasnov.brutus.streaming.StreamingRotation;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
@Getter
public class ConfigurationStreamManager {

    private final StreamingRotation streamingRotation;

    private ConfigurationManager configurationManager;

    private final MetricsManager metricsManager;

    public ConfigurationStreamManager(StreamingRotation streamingRotation, ConfigurationManager configurationManager, MetricsManager metricsManager) {
        this.streamingRotation = streamingRotation;
        this.configurationManager = configurationManager;
        this.metricsManager = metricsManager;
        this.overwriteConfig(configurationManager);
    }

    public void overwriteConfig(ConfigurationManager newConfig) {
        configurationManager = newConfig;
        streamingRotation.restartStreaming();
        this.metricsManager.updateConfig(newConfig);
    }
}

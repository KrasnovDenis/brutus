package com.krasnov.brutus.api;

import com.krasnov.brutus.api.filter.Filter;
import com.krasnov.brutus.api.filter.SimpleLogFilter;
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
    private final SimpleLogFilter filter;

    public ConfigurationStreamManager(StreamingRotation streamingRotation, ConfigurationManager configurationManager, MetricsManager metricsManager, SimpleLogFilter filter) {
        this.streamingRotation = streamingRotation;
        this.configurationManager = configurationManager;
        this.metricsManager = metricsManager;
        this.filter = filter;
        this.overwriteConfig(configurationManager);
    }

    public void overwriteConfig(ConfigurationManager newConfig) {
        configurationManager = newConfig;
        streamingRotation.restartStreaming();
        this.metricsManager.updateConfig(newConfig);
        this.filter.setFilterConfig(newConfig.getFilter());
    }
}

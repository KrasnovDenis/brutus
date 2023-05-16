package com.krasnov.brutus.api;

import com.krasnov.brutus.streaming.StreamingRotation;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
@Getter
public class ConfigurationStreamManager {

    private final StreamingRotation streamingRotation;

    private ConfigurationManager configurationManager;

    public ConfigurationStreamManager(StreamingRotation streamingRotation, ConfigurationManager configurationManager) {
        this.streamingRotation = streamingRotation;
        this.configurationManager = configurationManager;
        this.overwriteConfig(configurationManager);
    }

    public void overwriteConfig(ConfigurationManager newConfig) {
        configurationManager = newConfig;
        streamingRotation.restartStreaming();
    }
}

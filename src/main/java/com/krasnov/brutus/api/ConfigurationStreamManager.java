package com.krasnov.brutus.api;

import com.krasnov.brutus.streaming.StreamingRotation;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Getter
public class ConfigurationStreamManager {

    private final StreamingRotation streamingRotation;

    private final ConfigurationManager configurationManager;

    public ConfigurationStreamManager(StreamingRotation streamingRotation, ConfigurationManager configurationManager) {
        this.streamingRotation = streamingRotation;
        this.configurationManager = configurationManager;
        this.overwriteConfig(configurationManager.getInput());
    }

    public void overwriteConfig(Set<ConfigurationManager.InputSetting> newConfig) {
        configurationManager.setInput(newConfig);
        streamingRotation.restartStreaming();
    }
}

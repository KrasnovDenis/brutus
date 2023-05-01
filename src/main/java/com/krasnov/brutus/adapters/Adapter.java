package com.krasnov.brutus.adapters;

import com.krasnov.brutus.api.ConfigurationManager;

public interface Adapter {
    void startNewThreadStreaming(ConfigurationManager.LoggerEntity loggerSettings);
}

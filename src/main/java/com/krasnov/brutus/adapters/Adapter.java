package com.krasnov.brutus.adapters;

import com.krasnov.brutus.api.ConfigurationManager;

/**
 * Usually this interface implemented by classed for every
 * datasource which can accept stream of data logs
 * but currently supported only elastic
 * <p>
 * todo: remove hardcode from ElasticAdapter + rename properties
 */
public interface Adapter {
    void startNewThreadStreaming(ConfigurationManager.InputSetting loggerSettings);
}

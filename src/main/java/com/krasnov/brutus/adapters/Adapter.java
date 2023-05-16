package com.krasnov.brutus.adapters;

import com.krasnov.brutus.api.ConfigurationManager;
import com.krasnov.brutus.api.pojo.LoggerRecord;
import com.krasnov.brutus.metrics.MetricsResponse;

import java.io.IOException;
import java.util.List;

/**
 * Usually this interface implemented by classed for every
 * datasource which can accept stream of data logs
 * but currently supported only elastic
 * <p>
 * todo: remove hardcode from ElasticAdapter + rename properties
 */
public interface Adapter {
    void startNewThreadStreaming(ConfigurationManager.InputSetting loggerSettings);
    void sendAsMessage(LoggerRecord record) throws IOException;
    void sendAsMessage(List<MetricsResponse> list) throws IOException;
}

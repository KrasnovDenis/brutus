package com.krasnov.brutus.streaming;

import com.krasnov.brutus.adapters.Adapter;
import com.krasnov.brutus.api.ConfigurationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StreamingRotation {
    /**
     * адаптер, который будет пересылать стрим из указанной поды,
     * на указанный вывод (вывод должен настраиваться конфигом)
     * <p>
     **/

    private final Adapter adapter;
    private final ConfigurationManager configuration;

    public StreamingRotation(Adapter adapter, ConfigurationManager configuration) {
        this.adapter = adapter;
        this.configuration = configuration;
    }

    public void restartStreaming() {
        configuration.getInput().forEach(namespace -> {
            log.debug("Started new thread stream for {}", namespace.getNamespace());
            adapter.startNewThreadStreaming(namespace);
        });
    }
}

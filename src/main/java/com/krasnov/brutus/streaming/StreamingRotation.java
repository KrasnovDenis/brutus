    package com.krasnov.brutus.streaming;

import com.google.common.io.ByteStreams;
import com.krasnov.brutus.adapters.Adapter;
import com.krasnov.brutus.api.ConfigurationManager;
import io.kubernetes.client.PodLogs;
import io.kubernetes.client.openapi.models.V1Pod;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@AllArgsConstructor
public class StreamingRotation {
/**
 * адаптер, который будет пересылать стрим из указанной поды, 
 * на указанный вывод (вывод должен настраиваться конфигом)
 * 
 **/

    private final Adapter adapter;
    private final ConfigurationManager configuration;

    private void startStreaming () {
        configuration.getRepresentation().forEach(namespace -> {
            log.debug("Started new thread stream for {}", namespace.getNamespace());
            adapter.startNewThreadStreaming(namespace.getPods());
        })
    }
}

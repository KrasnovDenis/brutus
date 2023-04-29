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

@Service
@AllArgsConstructor
public class StreamingRotation {

    private final Adapter adapter;
    private final ConfigurationManager configuration;
    ExecutorService executorService = Executors.newFixedThreadPool(5, Thread::new);
    private void startStreaming () {

        configuration.getRepresentation().stream(item -> {
            adapter.startNewThreadStreaming(item, );

            executorService.execute(() -> {
                try {
                    V1Pod pod = api.readNamespacedPod("counter-1-55c8d66bcd-gm8rk", DEFAULT_NAMESPACE, "");
                    PodLogs podLogs = new PodLogs(client);
                    InputStream is = podLogs.streamNamespacedPodLog(pod);
                    ByteStreams.copy(is, System.out);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        })

    }

}

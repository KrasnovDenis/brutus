package com.krasnov.brutus.adapters.elastic;

import com.google.common.io.ByteStreams;
import com.krasnov.brutus.adapters.Adapter;
import com.krasnov.brutus.api.ConfigurationManager;
import com.krasnov.brutus.k8s.KubernetesConfiguration;
import io.kubernetes.client.PodLogs;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.krasnov.brutus.api.misc.Constants.THREAD_POOL_SIZE;

@Repository
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class ElasticAdapter implements Adapter {
    @Autowired
    private KubernetesConfiguration k8sService;
    private ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE, Thread::new);

    private List<Future<?>> streamingQueue = new LinkedList<>();

    @Override
    public void startNewThreadStreaming(ConfigurationManager.LoggerEntity loggerSettings) {
        completeOldThreads();
        ApiClient client = k8sService.getClient();
        CoreV1Api api = k8sService.getApi();
        for (String podName : loggerSettings.getPods()) {
            log.info("NEW THREAD STARTED FOR POD : {}", podName);
            Runnable startNewStream = () -> {
                try {
                    V1Pod pod = api.readNamespacedPod(podName, loggerSettings.getNamespace(), "");
                    PodLogs podLogs = new PodLogs(client);
                    InputStream stream = podLogs.streamNamespacedPodLog(pod);
                    sendToElastic(stream);
                } catch (Exception e) {
                    log.error("Streaming from k8s pod {} failed", podName);
                }
            };
            streamingQueue.add(executorService.submit(startNewStream));
        }
    }

    private void completeOldThreads() {
        streamingQueue.forEach(task -> task.cancel(true));
        streamingQueue.clear();
    }

    private void sendToElastic(InputStream stream) {
        try {
            ByteStreams.copy(stream, System.out);
        } catch (IOException ioException) {
            log.error("Can't send logs into system out (stub in ElasticAdapter)");
        }
    }
}

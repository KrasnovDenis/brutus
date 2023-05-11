package com.krasnov.brutus.adapters.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.google.common.base.Joiner;
import com.krasnov.brutus.adapters.Adapter;
import com.krasnov.brutus.api.ConfigurationManager;
import com.krasnov.brutus.api.pojo.LoggerRecord;
import com.krasnov.brutus.k8s.KubernetesConfiguration;
import io.kubernetes.client.PodLogs;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.krasnov.brutus.api.misc.Constants.THREAD_POOL_SIZE;

@Repository
@NoArgsConstructor
@Slf4j
public class ElasticAdapter implements Adapter {
    private KubernetesConfiguration k8sService;

    private ElasticsearchClient elasticsearchClient;
    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE, Thread::new);

    private final List<Future<?>> streamingQueue = new LinkedList<>();
    private final String elasticStreamName = "brutus-topic";

    @Autowired
    public ElasticAdapter(KubernetesConfiguration k8sService, ElasticsearchClient elasticsearchClient) {
        this.k8sService = k8sService;
        this.elasticsearchClient = elasticsearchClient;
    }

    @Override
    public void startNewThreadStreaming(ConfigurationManager.LoggerEntity loggerSettings) {
        completeOldThreads();
        ApiClient client = k8sService.getClient();
        CoreV1Api api = k8sService.getApi();
        for (String podName : loggerSettings.getPods()) {
            Runnable startNewStream = () -> {
                try {
                    V1Pod pod = api.readNamespacedPod(podName, loggerSettings.getNamespace(), "");
                    PodLogs podLogs = new PodLogs(client);
                    InputStream stream = podLogs.streamNamespacedPodLog(pod);
                    sendToElastic(stream, podName, loggerSettings.getNamespace());
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

    private void processStream(InputStream stream, ExecutorService executor, String podName, String namespace) throws IOException {
        while (true) {
            List<Character> payload = new LinkedList<>();
            char symbol = (char) stream.read();
            if (symbol != '\n') {
                while (symbol != '\n') {
                    payload.add(symbol);
                    symbol = (char) stream.read();
                }
            } else {
                symbol = (char) stream.read();
                payload.add(symbol);
            }

            Runnable r = () -> {
                try {
                    String logMessage = Joiner.on("").join(payload);
                    LoggerRecord loggerRecord = new LoggerRecord(logMessage, podName, namespace);
                    elasticsearchClient.index(i -> i.index(elasticStreamName)
                            .id("[RequestId=" + UUID.randomUUID() + "]")
                            .document(loggerRecord));
                } catch (IOException e) {
                    log.error("Can't send logs into outer elastic instance");
                }
            };
            executor.execute(r);
        }
    }

    private void sendToElastic(InputStream stream, String podName, String namespace) {
        try {
            processStream(stream, Executors.newSingleThreadExecutor(Thread::new), podName, namespace);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.krasnov.brutus.adapters.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.google.common.base.Joiner;
import com.krasnov.brutus.adapters.Adapter;
import com.krasnov.brutus.api.ConfigurationManager;
import com.krasnov.brutus.api.filter.Filter;
import com.krasnov.brutus.api.pojo.LoggerRecord;
import com.krasnov.brutus.k8s.KubernetesConfiguration;
import com.krasnov.brutus.metrics.MetricsResponse;
import io.kubernetes.client.PodLogs;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    private Filter filter;
   private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE, Thread::new);
    private final List<Future<?>> streamingQueue = new LinkedList<>();

    @Value("${brutus.logs.stream.name}")
    private String LOGS_STREAM;
    @Value("${brutus.metrics.stream.name}")
    private String METRICS_STREAM;

    @Autowired
    public ElasticAdapter(KubernetesConfiguration k8sService, ElasticsearchClient elasticsearchClient, Filter filter) {
        this.k8sService = k8sService;
        this.elasticsearchClient = elasticsearchClient;
        this.filter = filter;
    }

    @Override
    public void startNewThreadStreaming(ConfigurationManager.InputSetting loggerSettings) {
        completeOldThreads();
        ApiClient client = k8sService.getClient();
        CoreV1Api api = k8sService.getApi();
        for (String podName : loggerSettings.getPods()) {
            Runnable startNewStream = () -> {
                try {
                    V1Pod pod = api.readNamespacedPod(podName, loggerSettings.getNamespace(), "");
                    PodLogs podLogs = new PodLogs(client);
                    InputStream stream = podLogs.streamNamespacedPodLog(pod);
                    processStream(stream, podName, loggerSettings.getNamespace());
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


    /**
     * todo: fix a bug with start streaming from N pos.
     *
     * @param stream
     * @param podName
     * @param namespace
     * @throws IOException
     */
    private void processStream(InputStream stream, String podName, String namespace) throws IOException {
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
            String logMessage = Joiner.on("").join(payload);


            String date = df.format(new Date(System.currentTimeMillis()));
            Map<String, String> timestamp = new HashMap<>() {{
                put("@timestamp", date);
            }};
            LoggerRecord record = new LoggerRecord(logMessage, podName, namespace, UUID.randomUUID().toString(), timestamp);
            sendToElastic(record);
        }
    }

    private void sendToElastic(LoggerRecord row) {
        try {
            LoggerRecord record = filter.apply(row);
            if (record != null) {
                sendAsMessage(row);
            } else {
                log.trace("Log row rejected by filter");
            }
        } catch (IOException e) {
            log.error("Can't send logs into outer elastic instance");
        }
    }

    @Override
    public void sendAsMessage(LoggerRecord record) throws IOException {

        elasticsearchClient.index(i -> i.index(LOGS_STREAM)
                .id("[RequestId=" + UUID.randomUUID() + "]")
                .document(record));
    }

    @Override
    public void sendAsMessage(List<MetricsResponse> responseList) throws IOException {
        for (MetricsResponse metric : responseList) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            String date = df.format(new Date(System.currentTimeMillis()));
            metric.getMetrics().put("@timestamp", date);
            elasticsearchClient.index(i -> i.index(METRICS_STREAM)
                    .id("[RequestId=" + UUID.randomUUID() + "]")
                    .document(metric));
        }
    }
}

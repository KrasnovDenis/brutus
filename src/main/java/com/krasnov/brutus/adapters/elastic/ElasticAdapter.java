package com.krasnov.brutus.adapters.elastic;

import com.krasnov.brutus.adapters.Adapter;
import org.springframework.stereotype.Repository;

@Repository
public class ElasticAdapter implements Adapter {

    private ElasticsearchClientConfig clientConfig;

    @Override
    public void startNewThreadStreaming(LoggerEntity loggerSettings) {
            ExecutorService executorService = Executors
                            .newFixedThreadPool(loggerSettings.getPods().size(), Thread::new);
                            
            for(String podName: loggerSettings.getPods()) {
                executorService.execute(() -> {
                    try {
                        V1Pod pod = api.readNamespacedPod(podName, loggerSettings.getNamespace(), "");
                        PodLogs podLogs = new PodLogs(client);
                        InputStream is = podLogs.streamNamespacedPodLog(pod);
                        ByteStreams.copy(is, System.out);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
    }
}

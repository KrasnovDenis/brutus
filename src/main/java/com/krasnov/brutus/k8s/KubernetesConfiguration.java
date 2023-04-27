package com.krasnov.brutus.k8s;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;
import com.krasnov.brutus.api.ConfigurationManager;
import io.kubernetes.client.PodLogs;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Configuration
public class KubernetesConfiguration {

    private final ConfigurationManager configurationManager;
    private final ApiClient client;
    private final CoreV1Api api;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Value("${configuration.brutus.configmap-name}")
    private String configMapName;

    /**
     * Brutus should be deployed on special namespace per cluster.
     * For development convenient it hardcoded as default
     */
    private final String DEFAULT_NAMESPACE = "default";

    public KubernetesConfiguration(ConfigurationManager configurationManager) throws IOException {
        client = Config.defaultClient();
        api = new CoreV1Api(client);
        this.configurationManager = configurationManager;
    }

    public void initK8sClient() {
        ExecutorService executorService = Executors.newFixedThreadPool(5, Thread::new);
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
    }


    public void initConfigFromConfigMap() throws Exception {
        V1ConfigMap configMap = api.readNamespacedConfigMap(configMapName, DEFAULT_NAMESPACE, "");
        Map<String, String> data = configMap.getData();

        if (data != null) {
            String jsonUnmarshalled = data.get("brutus-config");
            List<ConfigurationManager.LoggerEntity> entityList = OBJECT_MAPPER.readValue(jsonUnmarshalled, new TypeReference<>() {});
            log.debug("Configmap brutus-config read");
            configurationManager.overwriteConfig(entityList);
        }
    }
}

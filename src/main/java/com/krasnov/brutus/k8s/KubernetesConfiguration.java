package com.krasnov.brutus.k8s;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.krasnov.brutus.api.ConfigurationManager;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.util.Config;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Slf4j
@Configuration
@Component
public class KubernetesConfiguration {
    @Getter
    private ApiClient client;
    @Getter
    private CoreV1Api api;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Value("${configuration.brutus.configmap-name}")
    private String configMapName;

    /**
     * Brutus should be deployed on special namespace per cluster.
     * For development convenient it hardcoded as default
     */
    private final String DEFAULT_NAMESPACE = "default";
    private final String BRUTUS_CONFIG = "brutus-config";

    public KubernetesConfiguration() {
        try {
            client = Config.defaultClient();
            api = new CoreV1Api(client);
        } catch (IOException e) {
            log.error("Impossible to connect to k8s, please check k8s config file {}", e.getMessage());
        }
    }


    /**
     * Search for config map in json format by name "brutus-config"
     * @return Bean with this configmap if present, null otherwise
     */
    @Bean
    public ConfigurationManager initConfigFromConfigMap() throws Exception {
        try {
            V1ConfigMap configMap = api.readNamespacedConfigMap(configMapName, DEFAULT_NAMESPACE, "");
            Map<String, String> data = configMap.getData();

            if (data != null) {
                String jsonUnmarshalled = data.get(BRUTUS_CONFIG);
                log.debug(jsonUnmarshalled);
                Set<ConfigurationManager.LoggerEntity> entityList = OBJECT_MAPPER.readValue(jsonUnmarshalled, new TypeReference<>() {
                });
                log.debug("Configmap brutus-config initialized");
                return new ConfigurationManager(entityList);
            }
        } catch (ApiException apiException) {
            log.error("Some kind of error appears while trying to access to k8s api {}", apiException.getMessage());
        }

        return null;
    }
}

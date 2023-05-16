package com.krasnov.brutus.metrics;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.krasnov.brutus.adapters.Adapter;
import com.krasnov.brutus.api.ConfigurationManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@EnableScheduling
@Component
public class MetricsManager {
    private final Set<String> endpointList;

    private final Adapter adapter;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Value("${brutus.metrics.timeout_response}")
    private long RESPONSE_TIMEOUT;

    public MetricsManager(ConfigurationManager configurationManager, Adapter adapter) {
        endpointList = configurationManager.getMetric();
        this.adapter = adapter;
    }

    @Scheduled(fixedRateString = "${brutus.metrics.interval}", initialDelay = 5000)
    private void fetchMetrics() throws IOException {
        List<MetricsResponse> metrics = new LinkedList<>();
        Set<WebClient> clientSet = prepareListOfClients();
        for (WebClient client : clientSet) {
            WebClient.ResponseSpec responseSpec = client.method(HttpMethod.GET)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve();

            String jsonResponse = responseSpec.bodyToMono(String.class).block(Duration.ofSeconds(RESPONSE_TIMEOUT));
            metrics.add(OBJECT_MAPPER.readValue(jsonResponse, new TypeReference<>() {}));
        }

        adapter.sendAsMessage(metrics);
    }

    private Set<WebClient> prepareListOfClients() {
        Set<WebClient> clientSet = new LinkedHashSet<>();
        for (String address : endpointList) {
            clientSet.add(WebClient.create(address));
        }

        return clientSet;
    }
}

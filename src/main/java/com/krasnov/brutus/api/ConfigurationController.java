package com.krasnov.brutus.api;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ConfigurationController {

    private final ConfigurationManager configuration;
    private final String CONFIGURATION_ENDPOINT = "/api/v1/brutus/config";

    public ConfigurationController(ConfigurationManager configuration) {
        this.configuration = configuration;
    }

    @GetMapping(CONFIGURATION_ENDPOINT)
    public ResponseEntity<?> getConfiguration() {
        return ResponseEntity.ok(configuration.getRepresentation());
    }

    @PostMapping(CONFIGURATION_ENDPOINT)
    public ResponseEntity<?> recreateConfig(@RequestBody List<ConfigurationManager.LoggerEntity> newConfig) {
        configuration.overwriteConfig(newConfig);
        return ResponseEntity.ok("Successfully updated!");
    }

}

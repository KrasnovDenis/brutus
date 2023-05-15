package com.krasnov.brutus.api;


import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static com.krasnov.brutus.api.misc.Constants.CONFIGURATION_ENDPOINT;

@RestController
@AllArgsConstructor
public class ConfigurationController {

    private final ConfigurationStreamManager configuration;

    @GetMapping(CONFIGURATION_ENDPOINT)
    public ResponseEntity<?> getConfiguration() {
        return ResponseEntity.ok(configuration.getConfigurationManager().getInput());
    }

    @PostMapping(CONFIGURATION_ENDPOINT)
    public ResponseEntity<?> recreateConfig(@RequestBody Set<ConfigurationManager.InputSetting> newConfig) {
        configuration.overwriteConfig(newConfig);
        return ResponseEntity.ok("Successfully updated!");
    }

}

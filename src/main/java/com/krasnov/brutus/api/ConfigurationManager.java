package com.krasnov.brutus.api;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ConfigurationManager {

    private Set<InputSetting> input;
    private Map<String, String> filter;

    @Data
    public static class InputSetting {
        private Set<String> pods;
        private String namespace;
    }
}

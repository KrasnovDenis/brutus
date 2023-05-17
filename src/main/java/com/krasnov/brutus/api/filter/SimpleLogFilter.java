package com.krasnov.brutus.api.filter;

import com.krasnov.brutus.api.ConfigurationManager;
import com.krasnov.brutus.api.pojo.LoggerRecord;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * Idea of this filter is just pass only rows of type LoggerRecord
 * If it suits by Configuration manager filter configuration
 * <p>
 * Worked by white-list if one of criteria matched then row passed
 */
@Component
@Slf4j
public class SimpleLogFilter implements Filter {

    @Setter
    @Getter
    private Map<String, String> filterConfig;

    public SimpleLogFilter(ConfigurationManager configurationManager) {
        this.filterConfig = configurationManager.getFilter();
    }

    @Override
    public LoggerRecord apply(LoggerRecord loggerRecord) {
        boolean passed = false;
        for (String type : filterConfig.keySet()) {
            //if already passed by another key
            if (passed) {
                break;
            }
            String value = filterConfig.get(type);
            try {
                passed = switch (FilterKeyType.valueOf(type)) {
                    case POD -> loggerRecord.getPodName().contains(value);
                    case NAMESPACE -> loggerRecord.getNamespace().contains(value);
                    case LOG -> loggerRecord.getLog().contains(value);
                };
            } catch (IllegalArgumentException ex) {
                log.warn("One of filter configuration condition with wrong spelling, please check the filter configuration");
            }
        }

        return (passed) ? loggerRecord : null;
    }
}

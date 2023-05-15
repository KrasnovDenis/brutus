package com.krasnov.brutus.api.filter;

import com.krasnov.brutus.api.pojo.LoggerRecord;

public interface Filter {
    LoggerRecord apply(LoggerRecord loggerRecord);
}

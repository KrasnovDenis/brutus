package com.krasnov.brutus.adapters.elastic;

import com.krasnov.brutus.adapters.Adapter;
import org.springframework.stereotype.Repository;

@Repository
public class ElasticAdapter implements Adapter {

    private ElasticsearchClientConfig clientConfig;

    @Override
    public void restartStreaming(Set<> pod) {

    }
}

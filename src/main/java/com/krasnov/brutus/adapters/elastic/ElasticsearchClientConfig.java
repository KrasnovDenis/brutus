package com.krasnov.brutus.adapters.elastic;
//
//import org.elasticsearch.client.RestHighLevelClient;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.elasticsearch.client.ClientConfiguration;
//import org.springframework.data.elasticsearch.client.RestClients;
//import org.springframework.data.elasticsearch.client.erhlc.AbstractElasticsearchConfiguration;
//import org.springframework.data.elasticsearch.client.erhlc.RestClients;
//import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
//
//@Configuration
//@EnableElasticsearchRepositories(basePackages = "com.krasnov.brutus.adapters.elastic")
//@ComponentScan(basePackages = { "com.krasnov.brutus.adapters.elastic" })
public class ElasticsearchClientConfig
//        extends AbstractElasticsearchConfiguration
{

//    @Bean
//    @Override
//    public RestHighLevelClient elasticsearchClient() {
//        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
//                .connectedTo("localhost:9200")
//                .build();
//
//        return RestClients.create(clientConfiguration)
//                .rest();
//    }
}
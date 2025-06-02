package com.chairpick.ecommerce.config;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QdrantConfig {

    @Value("${qdrant.host}")
    private String qdrantHost;

    @Value("${qdrant.port}")
    private int qdrantPort;


    @Bean
    QdrantClient qdrantClient() {
        return new QdrantClient(QdrantGrpcClient.newBuilder(
                qdrantHost, qdrantPort, false).build());
    }

}

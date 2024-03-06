package com.csfrez.tool.minio;


import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient(MinioProperties properties) {
        try {
            MinioClient.Builder builder = MinioClient.builder();
            builder.endpoint(properties.getEndpoint());
            if (StringUtils.hasLength(properties.getAccessKey()) && StringUtils.hasLength(properties.getSecretKey())) {
                builder.credentials(properties.getAccessKey(), properties.getSecretKey());
            }
            return builder.build();
        } catch (Exception e) {
            return null;
        }
    }
}
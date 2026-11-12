package br.com.beneficiosonline.integration.aws;

import java.net.URI;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
@EnableConfigurationProperties(AwsProperties.class)
@ConditionalOnProperty(name = "app.aws.enabled", havingValue = "true")
public class AwsMessagingConfig {

    @Bean
    SnsClient snsClient(AwsProperties properties) {
        var builder = SnsClient.builder().region(Region.of(properties.getRegion()))
                .credentialsProvider(DefaultCredentialsProvider.create());
        if (properties.getEndpoint() != null && !properties.getEndpoint().isBlank()) {
            builder.endpointOverride(URI.create(properties.getEndpoint()));
        }
        return builder.build();
    }

    @Bean
    SqsClient sqsClient(AwsProperties properties) {
        var builder = SqsClient.builder().region(Region.of(properties.getRegion()))
                .credentialsProvider(DefaultCredentialsProvider.create());
        if (properties.getEndpoint() != null && !properties.getEndpoint().isBlank()) {
            builder.endpointOverride(URI.create(properties.getEndpoint()));
        }
        return builder.build();
    }
}

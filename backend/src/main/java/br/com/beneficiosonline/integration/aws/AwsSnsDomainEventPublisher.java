package br.com.beneficiosonline.integration.aws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import br.com.beneficiosonline.integration.events.DomainEventPublisher;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Component
@ConditionalOnProperty(name = "app.aws.enabled", havingValue = "true")
public class AwsSnsDomainEventPublisher implements DomainEventPublisher {
    private final SnsClient snsClient;
    private final AwsProperties properties;
    private final ObjectMapper objectMapper;

    public AwsSnsDomainEventPublisher(SnsClient snsClient, AwsProperties properties, ObjectMapper objectMapper) {
        this.snsClient = snsClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(Object event) {
        try {
            snsClient.publish(PublishRequest.builder().topicArn(properties.getSnsTopicArn())
                    .message(objectMapper.writeValueAsString(event)).build());
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Não foi possível serializar o evento de domínio", exception);
        }
    }
}

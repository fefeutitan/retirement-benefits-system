package br.com.beneficiosonline.integration.aws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

@Component
@ConditionalOnProperty(name = "app.aws.enabled", havingValue = "true")
public class SqsDomainEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(SqsDomainEventConsumer.class);
    private final SqsClient sqsClient;
    private final AwsProperties properties;
    private final ObjectMapper objectMapper;

    public SqsDomainEventConsumer(SqsClient sqsClient, AwsProperties properties, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelayString = "${app.aws.consumer-delay-ms:2000}")
    public void consume() {
        var response = sqsClient.receiveMessage(ReceiveMessageRequest.builder()
                .queueUrl(properties.getSqsQueueUrl()).maxNumberOfMessages(10).waitTimeSeconds(1).build());
        response.messages().forEach(message -> {
            try {
                JsonNode envelope = objectMapper.readTree(message.body());
                String eventJson = envelope.has("Message") ? envelope.get("Message").asText() : message.body();
                log.info("Evento recebido da SQS: {}", objectMapper.readTree(eventJson));
                sqsClient.deleteMessage(DeleteMessageRequest.builder().queueUrl(properties.getSqsQueueUrl())
                        .receiptHandle(message.receiptHandle()).build());
            } catch (Exception exception) {
                log.error("Falha ao processar mensagem SQS; ela ficará disponível para retry", exception);
            }
        });
    }
}

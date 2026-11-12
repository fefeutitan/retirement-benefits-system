package br.com.beneficiosonline.integration.aws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.beneficiosonline.integration.events.IdempotentEventHandler;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.MessageSystemAttributeName;

@Component
@ConditionalOnProperty(name = "app.aws.enabled", havingValue = "true")
public class SqsDomainEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(SqsDomainEventConsumer.class);
    private final SqsClient sqsClient;
    private final AwsProperties properties;
    private final ObjectMapper objectMapper;
    private final IdempotentEventHandler eventHandler;

    public SqsDomainEventConsumer(SqsClient sqsClient, AwsProperties properties, ObjectMapper objectMapper,
            IdempotentEventHandler eventHandler) {
        this.sqsClient = sqsClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.eventHandler = eventHandler;
    }

    @Scheduled(fixedDelayString = "${app.aws.consumer-delay-ms:2000}")
    public void consume() {
        var response = sqsClient.receiveMessage(ReceiveMessageRequest.builder()
                .queueUrl(properties.getSqsQueueUrl()).maxNumberOfMessages(10).waitTimeSeconds(1)
                .messageSystemAttributeNames(MessageSystemAttributeName.APPROXIMATE_RECEIVE_COUNT).build());
        response.messages().forEach(message -> {
            try {
                JsonNode envelope = objectMapper.readTree(message.body());
                String eventJson = envelope.has("Message") ? envelope.get("Message").asText() : message.body();
                JsonNode event = objectMapper.readTree(eventJson);
                boolean processed = eventHandler.process(event);
                log.info("Evento recebido da SQS (tentativa {}, novo={}): {}",
                        message.attributes().get(MessageSystemAttributeName.APPROXIMATE_RECEIVE_COUNT),
                        processed, event);
                sqsClient.deleteMessage(DeleteMessageRequest.builder().queueUrl(properties.getSqsQueueUrl())
                        .receiptHandle(message.receiptHandle()).build());
            } catch (Exception exception) {
                log.error("Falha ao processar mensagem SQS; ela ficará disponível para retry", exception);
            }
        });
    }
}

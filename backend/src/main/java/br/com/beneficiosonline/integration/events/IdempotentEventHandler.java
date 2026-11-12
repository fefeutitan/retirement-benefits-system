package br.com.beneficiosonline.integration.events;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IdempotentEventHandler {
    private final ProcessedEventRepository repository;

    public IdempotentEventHandler(ProcessedEventRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public boolean process(JsonNode event) {
        String eventId = event.path("eventId").asText(null);
        String eventType = event.path("eventType").asText("UnknownEvent");
        if (eventId == null || eventId.isBlank()) {
            throw new IllegalArgumentException("Evento sem eventId não pode ser processado com segurança");
        }
        if (repository.existsByEventId(eventId)) {
            return false;
        }

        // Efeitos de negócio futuros devem ocorrer antes do registro da confirmação.
        repository.saveAndFlush(new ProcessedEvent(eventId, eventType));
        return true;
    }
}

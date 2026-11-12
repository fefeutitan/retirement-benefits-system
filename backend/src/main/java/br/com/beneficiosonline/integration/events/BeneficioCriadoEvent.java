package br.com.beneficiosonline.integration.events;

import java.time.Instant;
import java.util.UUID;

public record BeneficioCriadoEvent(String eventId, String eventType, Long beneficioId, String servidorNome, Instant occurredAt) {
    public static BeneficioCriadoEvent of(Long id, String nome) {
        return new BeneficioCriadoEvent(UUID.randomUUID().toString(), "BeneficioCriado", id, nome, Instant.now());
    }
}

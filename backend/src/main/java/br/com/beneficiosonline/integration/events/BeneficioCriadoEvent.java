package br.com.beneficiosonline.integration.events;

import java.time.Instant;

public record BeneficioCriadoEvent(String eventType, Long beneficioId, String servidorNome, Instant occurredAt) {
    public static BeneficioCriadoEvent of(Long id, String nome) {
        return new BeneficioCriadoEvent("BeneficioCriado", id, nome, Instant.now());
    }
}

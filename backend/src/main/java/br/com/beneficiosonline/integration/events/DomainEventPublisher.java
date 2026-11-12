package br.com.beneficiosonline.integration.events;

public interface DomainEventPublisher {
    void publish(Object event);
}

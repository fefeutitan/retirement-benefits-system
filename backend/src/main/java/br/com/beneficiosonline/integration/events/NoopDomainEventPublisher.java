package br.com.beneficiosonline.integration.events;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.aws.enabled", havingValue = "false", matchIfMissing = true)
public class NoopDomainEventPublisher implements DomainEventPublisher {
    @Override
    public void publish(Object event) {
        // AWS is disabled for the default H2 development profile.
    }
}

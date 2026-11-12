package br.com.beneficiosonline;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.beneficiosonline.integration.events.IdempotentEventHandler;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
class BeneficiosOnlineApplicationTests {

    @Autowired
    private IdempotentEventHandler eventHandler;

    @Test
    void contextLoads() {
    }

    @Test
    void duplicateEventIsIgnored() throws Exception {
        var event = new ObjectMapper().readTree("{\"eventId\":\"event-123\",\"eventType\":\"BeneficioCriado\"}");
        org.junit.jupiter.api.Assertions.assertTrue(eventHandler.process(event));
        org.junit.jupiter.api.Assertions.assertFalse(eventHandler.process(event));
    }
}

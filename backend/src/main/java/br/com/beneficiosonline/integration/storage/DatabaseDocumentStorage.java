package br.com.beneficiosonline.integration.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import br.com.beneficiosonline.domain.documento.Documento;

@Component
@ConditionalOnProperty(name = "app.aws.enabled", havingValue = "false", matchIfMissing = true)
public class DatabaseDocumentStorage implements DocumentStorage {
    @Override
    public void store(String key, String contentType, byte[] content) {
        // The default H2 profile keeps PDF bytes in the database.
    }

    @Override
    public byte[] load(Documento documento) {
        return documento.getConteudo();
    }
}

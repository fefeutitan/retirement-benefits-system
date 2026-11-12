package br.com.beneficiosonline.integration.storage;

import br.com.beneficiosonline.domain.documento.Documento;

public interface DocumentStorage {
    void store(String key, String contentType, byte[] content);
    byte[] load(Documento documento);
}

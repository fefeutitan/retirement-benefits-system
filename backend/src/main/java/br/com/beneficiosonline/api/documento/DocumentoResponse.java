package br.com.beneficiosonline.api.documento;

import java.time.Instant;

import br.com.beneficiosonline.domain.documento.Documento;
import br.com.beneficiosonline.domain.documento.DocumentoCategoria;

public record DocumentoResponse(Long id, String nomeArquivo, String tipoMime, long tamanho,
        DocumentoCategoria categoria, Instant anexadoEm) {

    public static DocumentoResponse from(Documento documento) {
        return new DocumentoResponse(documento.getId(), documento.getNomeArquivo(), documento.getTipoMime(),
                documento.getTamanho(), documento.getCategoria(), documento.getAnexadoEm());
    }
}

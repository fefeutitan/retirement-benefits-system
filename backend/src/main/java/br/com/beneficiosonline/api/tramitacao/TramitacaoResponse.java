package br.com.beneficiosonline.api.tramitacao;

import java.time.Instant;

import br.com.beneficiosonline.domain.tramitacao.Tramitacao;

public record TramitacaoResponse(Long id, String setorOrigem, String setorDestino, String responsavel,
        String observacao, Instant tramitadoEm) {

    public static TramitacaoResponse from(Tramitacao tramitacao) {
        return new TramitacaoResponse(tramitacao.getId(), tramitacao.getSetorOrigem(), tramitacao.getSetorDestino(),
                tramitacao.getResponsavel(), tramitacao.getObservacao(), tramitacao.getTramitadoEm());
    }
}

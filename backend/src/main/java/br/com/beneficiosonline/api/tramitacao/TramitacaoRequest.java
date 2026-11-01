package br.com.beneficiosonline.api.tramitacao;

import jakarta.validation.constraints.NotBlank;

public record TramitacaoRequest(
        @NotBlank String setorDestino,
        @NotBlank String responsavel,
        String observacao) {
}

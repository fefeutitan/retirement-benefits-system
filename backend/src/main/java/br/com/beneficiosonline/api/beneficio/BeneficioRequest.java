package br.com.beneficiosonline.api.beneficio;

import jakarta.validation.constraints.NotBlank;

public record BeneficioRequest(
        @NotBlank String servidorNome,
        @NotBlank String servidorCpf,
        @NotBlank String servidorOrgao,
        @NotBlank String servidorMatricula) {
}

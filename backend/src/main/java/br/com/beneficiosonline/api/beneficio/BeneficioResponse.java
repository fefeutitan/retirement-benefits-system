package br.com.beneficiosonline.api.beneficio;

import java.time.Instant;

import br.com.beneficiosonline.domain.beneficio.Beneficio;
import br.com.beneficiosonline.domain.beneficio.BeneficioStatus;

public record BeneficioResponse(Long id, String servidorNome, String servidorCpf, String servidorOrgao,
        String servidorMatricula, BeneficioStatus status, Instant criadoEm) {

    public static BeneficioResponse from(Beneficio beneficio) {
        return new BeneficioResponse(beneficio.getId(), beneficio.getServidorNome(), beneficio.getServidorCpf(),
                beneficio.getServidorOrgao(), beneficio.getServidorMatricula(), beneficio.getStatus(),
                beneficio.getCriadoEm());
    }
}

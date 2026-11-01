package br.com.beneficiosonline.api.beneficio;

import br.com.beneficiosonline.domain.beneficio.BeneficioStatus;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(@NotNull BeneficioStatus status) {
}

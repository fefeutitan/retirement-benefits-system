package br.com.beneficiosonline.domain.tramitacao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TramitacaoRepository extends JpaRepository<Tramitacao, Long> {
    List<Tramitacao> findByBeneficioIdOrderByTramitadoEmDesc(Long beneficioId);
}

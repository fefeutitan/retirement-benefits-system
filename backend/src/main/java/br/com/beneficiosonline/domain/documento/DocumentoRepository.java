package br.com.beneficiosonline.domain.documento;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    List<Documento> findByBeneficioIdOrderByAnexadoEmDesc(Long beneficioId);
    Optional<Documento> findByIdAndBeneficioId(Long id, Long beneficioId);
}

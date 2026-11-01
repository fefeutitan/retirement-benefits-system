package br.com.beneficiosonline.domain.documento;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    List<Documento> findByBeneficioIdOrderByAnexadoEmDesc(Long beneficioId);
}

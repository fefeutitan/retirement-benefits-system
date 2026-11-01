package br.com.beneficiosonline.domain.beneficio;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BeneficioRepository extends JpaRepository<Beneficio, Long> {
    List<Beneficio> findByServidorNomeContainingIgnoreCase(String servidorNome);
}

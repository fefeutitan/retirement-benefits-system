package br.com.beneficiosonline.domain.tramitacao;

import java.time.Instant;

import br.com.beneficiosonline.domain.beneficio.Beneficio;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tramitacao")
public class Tramitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String setorOrigem;
    @Column(nullable = false)
    private String setorDestino;
    @Column(nullable = false)
    private String responsavel;
    private String observacao;
    @Column(nullable = false)
    private Instant tramitadoEm;

    @ManyToOne
    @JoinColumn(name = "beneficio_id", nullable = false)
    private Beneficio beneficio;

    protected Tramitacao() {
    }

    public Tramitacao(String setorOrigem, String setorDestino, String responsavel, String observacao) {
        this.setorOrigem = setorOrigem;
        this.setorDestino = setorDestino;
        this.responsavel = responsavel;
        this.observacao = observacao;
        this.tramitadoEm = Instant.now();
    }

    public void vincularAo(Beneficio beneficio) { this.beneficio = beneficio; }
    public Long getId() { return id; }
    public String getSetorOrigem() { return setorOrigem; }
    public String getSetorDestino() { return setorDestino; }
    public String getResponsavel() { return responsavel; }
    public String getObservacao() { return observacao; }
    public Instant getTramitadoEm() { return tramitadoEm; }
}

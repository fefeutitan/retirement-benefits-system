package br.com.beneficiosonline.domain.beneficio;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import br.com.beneficiosonline.domain.documento.Documento;
import br.com.beneficiosonline.domain.tramitacao.Tramitacao;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "beneficio")
public class Beneficio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String servidorNome;
    @Column(nullable = false)
    private String servidorCpf;
    @Column(nullable = false)
    private String servidorOrgao;
    @Column(nullable = false)
    private String servidorMatricula;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BeneficioStatus status = BeneficioStatus.EM_ELABORACAO;

    @Column(nullable = false)
    private Instant criadoEm;

    @OneToMany(mappedBy = "beneficio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Documento> documentos = new ArrayList<>();

    @OneToMany(mappedBy = "beneficio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tramitacao> tramitacoes = new ArrayList<>();

    protected Beneficio() {
    }

    public Beneficio(String servidorNome, String servidorCpf, String servidorOrgao, String servidorMatricula) {
        this.servidorNome = servidorNome;
        this.servidorCpf = servidorCpf;
        this.servidorOrgao = servidorOrgao;
        this.servidorMatricula = servidorMatricula;
        this.criadoEm = Instant.now();
    }

    public Long getId() { return id; }
    public String getServidorNome() { return servidorNome; }
    public String getServidorCpf() { return servidorCpf; }
    public String getServidorOrgao() { return servidorOrgao; }
    public String getServidorMatricula() { return servidorMatricula; }
    public BeneficioStatus getStatus() { return status; }
    public Instant getCriadoEm() { return criadoEm; }
    public List<Documento> getDocumentos() { return documentos; }
    public List<Tramitacao> getTramitacoes() { return tramitacoes; }

    public void alterarStatus(BeneficioStatus novoStatus) {
        if (novoStatus == null || novoStatus == status) return;
        boolean permitido = (status == BeneficioStatus.EM_ELABORACAO && novoStatus == BeneficioStatus.EM_ANALISE)
                || (status == BeneficioStatus.EM_ANALISE && novoStatus == BeneficioStatus.CONCLUIDO)
                || (status == BeneficioStatus.CONCLUIDO && novoStatus == BeneficioStatus.ARQUIVADO);
        if (!permitido) {
            throw new IllegalStateException("Transição de status não permitida: " + status + " -> " + novoStatus);
        }
        status = novoStatus;
    }

    public void adicionarDocumento(Documento documento) {
        documentos.add(documento);
        documento.vincularAo(this);
    }

    public void adicionarTramitacao(Tramitacao tramitacao) {
        tramitacoes.add(tramitacao);
        tramitacao.vincularAo(this);
        status = BeneficioStatus.EM_ANALISE;
    }
}

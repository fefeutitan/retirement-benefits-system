package br.com.beneficiosonline.domain.documento;

import java.time.Instant;

import br.com.beneficiosonline.domain.beneficio.Beneficio;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "documento")
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeArquivo;
    @Column(nullable = false)
    private String tipoMime;
    @Column(nullable = false)
    private long tamanho;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentoCategoria categoria;

    @Column(nullable = true, columnDefinition = "BYTEA")
    private byte[] conteudo;

    @Column(name = "s3_key", length = 500)
    private String s3Key;

    @Column(nullable = false)
    private Instant anexadoEm;

    @ManyToOne
    @JoinColumn(name = "beneficio_id", nullable = false)
    private Beneficio beneficio;

    protected Documento() {
    }

    public Documento(String nomeArquivo, String tipoMime, DocumentoCategoria categoria, byte[] conteudo) {
        this.nomeArquivo = nomeArquivo;
        this.tipoMime = tipoMime;
        this.categoria = categoria;
        this.conteudo = conteudo;
        this.tamanho = conteudo.length;
        this.anexadoEm = Instant.now();
    }

    public Documento(String nomeArquivo, String tipoMime, DocumentoCategoria categoria, byte[] conteudo, String s3Key) {
        this(nomeArquivo, tipoMime, categoria, conteudo);
        this.s3Key = s3Key;
    }

    public void vincularAo(Beneficio beneficio) { this.beneficio = beneficio; }
    public Long getId() { return id; }
    public String getNomeArquivo() { return nomeArquivo; }
    public String getTipoMime() { return tipoMime; }
    public long getTamanho() { return tamanho; }
    public DocumentoCategoria getCategoria() { return categoria; }
    public byte[] getConteudo() { return conteudo; }
    public String getS3Key() { return s3Key; }
    public Instant getAnexadoEm() { return anexadoEm; }
}

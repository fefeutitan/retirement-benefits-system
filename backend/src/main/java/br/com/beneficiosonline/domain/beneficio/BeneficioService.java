package br.com.beneficiosonline.domain.beneficio;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.beneficiosonline.api.beneficio.BeneficioRequest;
import br.com.beneficiosonline.domain.documento.Documento;
import br.com.beneficiosonline.domain.documento.DocumentoCategoria;
import br.com.beneficiosonline.domain.documento.DocumentoRepository;
import br.com.beneficiosonline.domain.tramitacao.Tramitacao;
import br.com.beneficiosonline.domain.tramitacao.TramitacaoRepository;

@Service
public class BeneficioService {

    private final BeneficioRepository beneficioRepository;
    private final DocumentoRepository documentoRepository;
    private final TramitacaoRepository tramitacaoRepository;

    public BeneficioService(BeneficioRepository beneficioRepository, DocumentoRepository documentoRepository,
            TramitacaoRepository tramitacaoRepository) {
        this.beneficioRepository = beneficioRepository;
        this.documentoRepository = documentoRepository;
        this.tramitacaoRepository = tramitacaoRepository;
    }

    @Transactional
    public Beneficio criar(BeneficioRequest request) {
        return beneficioRepository.save(new Beneficio(request.servidorNome(), request.servidorCpf(),
                request.servidorOrgao(), request.servidorMatricula()));
    }

    @Transactional(readOnly = true)
    public List<Beneficio> listar(String servidor) {
        return servidor == null || servidor.isBlank() ? beneficioRepository.findAll()
                : beneficioRepository.findByServidorNomeContainingIgnoreCase(servidor);
    }

    @Transactional(readOnly = true)
    public Beneficio buscar(Long id) { return beneficioRepository.findById(id).orElseThrow(() -> naoEncontrado("Benefício")); }

    @Transactional
    public Beneficio alterarStatus(Long id, BeneficioStatus status) {
        Beneficio beneficio = buscar(id);
        try {
            beneficio.alterarStatus(status);
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage());
        }
        return beneficioRepository.save(beneficio);
    }

    @Transactional
    public Documento anexarDocumento(Long beneficioId, DocumentoCategoria categoria, String nomeArquivo,
            String tipoMime, byte[] conteudo) {
        if (!"application/pdf".equalsIgnoreCase(tipoMime) || !nomeArquivo.toLowerCase().endsWith(".pdf")) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Apenas arquivos PDF são aceitos");
        }
        if (conteudo.length == 0 || conteudo.length > 10 * 1024 * 1024) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O documento não pode estar vazio");
        }
        if (conteudo.length < 5 || !(conteudo[0] == '%' && conteudo[1] == 'P' && conteudo[2] == 'D'
                && conteudo[3] == 'F' && conteudo[4] == '-')) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "O conteúdo não é um PDF válido");
        }
        Beneficio beneficio = buscar(beneficioId);
        Documento documento = new Documento(nomeArquivo, tipoMime, categoria, conteudo);
        beneficio.adicionarDocumento(documento);
        beneficioRepository.save(beneficio);
        return documento;
    }

    @Transactional(readOnly = true)
    public List<Documento> listarDocumentos(Long beneficioId) {
        buscar(beneficioId);
        return documentoRepository.findByBeneficioIdOrderByAnexadoEmDesc(beneficioId);
    }

    @Transactional
    public Tramitacao tramitar(Long beneficioId, String setorDestino, String responsavel, String observacao) {
        Beneficio beneficio = buscar(beneficioId);
        String setorOrigem = tramitacaoRepository.findByBeneficioIdOrderByTramitadoEmDesc(beneficioId).stream()
                .findFirst().map(Tramitacao::getSetorDestino).orElse("Protocolo");
        Tramitacao tramitacao = new Tramitacao(setorOrigem, setorDestino, responsavel, observacao);
        beneficio.adicionarTramitacao(tramitacao);
        beneficioRepository.save(beneficio);
        return tramitacao;
    }

    @Transactional(readOnly = true)
    public List<Tramitacao> listarTramitacoes(Long beneficioId) {
        buscar(beneficioId);
        return tramitacaoRepository.findByBeneficioIdOrderByTramitadoEmDesc(beneficioId);
    }

    private ResponseStatusException naoEncontrado(String recurso) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, recurso + " não encontrado");
    }
}

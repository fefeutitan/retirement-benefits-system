package br.com.beneficiosonline.api.documento;

import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.beneficiosonline.domain.beneficio.BeneficioService;
import br.com.beneficiosonline.domain.documento.Documento;
import br.com.beneficiosonline.domain.documento.DocumentoCategoria;

@RestController
@RequestMapping("/api/beneficios/{beneficioId}/documentos")
public class DocumentoController {

    private final BeneficioService service;

    public DocumentoController(BeneficioService service) { this.service = service; }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DocumentoResponse anexar(@PathVariable Long beneficioId, @RequestParam DocumentoCategoria categoria,
            @RequestParam MultipartFile arquivo) throws java.io.IOException {
        Documento documento = service.anexarDocumento(beneficioId, categoria, arquivo.getOriginalFilename(),
                arquivo.getContentType(), arquivo.getBytes());
        return DocumentoResponse.from(documento);
    }

    @GetMapping
    public List<DocumentoResponse> listar(@PathVariable Long beneficioId) {
        return service.listarDocumentos(beneficioId).stream().map(DocumentoResponse::from).toList();
    }

    @GetMapping("/{documentoId}/conteudo")
    public ResponseEntity<ByteArrayResource> baixar(@PathVariable Long beneficioId, @PathVariable Long documentoId) {
        Documento documento = service.listarDocumentos(beneficioId).stream()
                .filter(item -> item.getId().equals(documentoId)).findFirst()
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Documento não encontrado"));
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(documento.getTipoMime()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + documento.getNomeArquivo() + "\"")
                .body(new ByteArrayResource(documento.getConteudo()));
    }
}

package br.com.beneficiosonline.api.tramitacao;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.beneficiosonline.domain.beneficio.BeneficioService;
import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/beneficios/{beneficioId}/tramitacoes")
public class TramitacaoController {

    private final BeneficioService service;

    public TramitacaoController(BeneficioService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TramitacaoResponse tramitar(@PathVariable Long beneficioId, @Valid @RequestBody TramitacaoRequest request) {
        return TramitacaoResponse.from(service.tramitar(beneficioId, request.setorDestino(), request.responsavel(),
                request.observacao()));
    }

    @GetMapping
    public List<TramitacaoResponse> listar(@PathVariable Long beneficioId) {
        return service.listarTramitacoes(beneficioId).stream().map(TramitacaoResponse::from).toList();
    }
}

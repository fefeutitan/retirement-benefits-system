package br.com.beneficiosonline.api.beneficio;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;

import br.com.beneficiosonline.domain.beneficio.BeneficioService;
import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/beneficios")
public class BeneficioController {

    private final BeneficioService service;

    public BeneficioController(BeneficioService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BeneficioResponse criar(@Valid @RequestBody BeneficioRequest request) {
        return BeneficioResponse.from(service.criar(request));
    }

    @GetMapping
    public List<BeneficioResponse> listar(@RequestParam(required = false) String servidor) {
        return service.listar(servidor).stream().map(BeneficioResponse::from).toList();
    }

    @GetMapping("/{id}")
    public BeneficioResponse buscar(@PathVariable Long id) {
        return BeneficioResponse.from(service.buscar(id));
    }

    @PostMapping("/{id}/status")
    public BeneficioResponse alterarStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        return BeneficioResponse.from(service.alterarStatus(id, request.status()));
    }
}

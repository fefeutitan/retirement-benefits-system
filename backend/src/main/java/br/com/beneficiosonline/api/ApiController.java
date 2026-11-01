package br.com.beneficiosonline.api;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class ApiController {

    @GetMapping("/status")
    public Map<String, String> status() {
        return Map.of("application", "beneficios_online", "status", "ok");
    }
}

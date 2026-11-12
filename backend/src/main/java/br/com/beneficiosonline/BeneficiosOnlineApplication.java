package br.com.beneficiosonline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BeneficiosOnlineApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeneficiosOnlineApplication.class, args);
    }
}

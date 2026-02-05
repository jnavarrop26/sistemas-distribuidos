package org.sistemasdistribuidos.correo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"controller", "services"})
public class CorreoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CorreoApplication.class, args);
    }

}

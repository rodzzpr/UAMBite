package com.example.uambite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class UAMBiteApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(UAMBiteApplication.class);
        app.addListeners((ApplicationListener<ApplicationEnvironmentPreparedEvent>) event -> {
            Environment env = event.getEnvironment();
            validarSecretosObligatorios(env);
        });
        app.run(args);
    }

    static void validarSecretosObligatorios(Environment env) {
        String jwtSecret = env.getProperty("jwt.secret");
        String dbPassword = env.getProperty("spring.datasource.password");
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalStateException(
                    "La variable de entorno JWT_SECRET es obligatoria. "
                    + "Define un secreto de al menos 32 bytes para HMAC-SHA256.");
        }
        if (dbPassword == null || dbPassword.isBlank()) {
            throw new IllegalStateException(
                    "La variable de entorno DATASOURCE_PASSWORD es obligatoria.");
        }
    }
}

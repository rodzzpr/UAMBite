package com.example.uambite.config;

import com.example.uambite.model.Rol;
import com.example.uambite.model.Usuario;
import com.example.uambite.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class AdminBootstrap implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);
    private static final Pattern CARNET_PATTERN = Pattern.compile("^[A-Za-z0-9]{4,20}$");
    private static final String TEMP_CHARS =
            "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
    private static final int TEMP_LENGTH = 12;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.carnet:ADMIN001}")
    private String carnet;

    @Value("${app.admin.nombre:Admin}")
    private String nombre;

    @Value("${app.admin.apellido:Sistema}")
    private String apellido;

    @Value("${app.admin.correo:admin@uambite.local}")
    private String correo;

    @Override
    @Transactional
    public void run(String... args) {
        long adminCount = repository.countByRol(Rol.ADMIN);
        if (adminCount > 0) {
            log.info("AdminBootstrap: ya existe al menos un usuario con rol ADMIN (count={}). No se crea admin inicial.",
                    adminCount);
            return;
        }

        if (!CARNET_PATTERN.matcher(carnet).matches()) {
            log.warn("AdminBootstrap: app.admin.carnet='{}' no cumple el formato (4-20 alfanum). No se crea admin inicial.",
                    carnet);
            return;
        }

        if (repository.findByCarnet(carnet).isPresent()) {
            log.warn("AdminBootstrap: el carnet '{}' ya existe. No se crea admin para evitar pisar.", carnet);
            return;
        }

        String tempPassword = generarPasswordTemporal();

        Usuario admin = Usuario.builder()
                .carnet(carnet)
                .nombre(nombre)
                .apellido(apellido)
                .correo(correo)
                .password(passwordEncoder.encode(tempPassword))
                .passwordTemporal(true)
                .rol(Rol.ADMIN)
                .build();
        repository.save(admin);

        log.warn("================================================================================");
        log.warn(" UAMBite - ADMIN INICIAL CREADO (CONTRASEÑA TEMPORAL)");
        log.warn("--------------------------------------------------------------------------------");
        log.warn("   carnet:        {}", carnet);
        log.warn("   password:      {}", tempPassword);
        log.warn("--------------------------------------------------------------------------------");
        log.warn(" Esta contraseña aparece UNA SOLA VEZ en los logs. Cópiala, inicia sesión");
        log.warn(" con ella y se te pedirá que la cambies antes de continuar.");
        log.warn("================================================================================");
    }

    private String generarPasswordTemporal() {
        StringBuilder sb = new StringBuilder(TEMP_LENGTH);
        for (int i = 0; i < TEMP_LENGTH; i++) {
            sb.append(TEMP_CHARS.charAt(RANDOM.nextInt(TEMP_CHARS.length())));
        }
        return sb.toString();
    }
}

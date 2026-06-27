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

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class AdminBootstrap implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);
    private static final Pattern CARNET_PATTERN = Pattern.compile("^[A-Za-z0-9]{4,20}$");

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.carnet:ADMIN001}")
    private String carnet;

    @Value("${app.admin.password:admin123}")
    private String password;

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
        if (password == null || password.length() < 6 || password.length() > 100) {
            log.warn("AdminBootstrap: app.admin.password no cumple el largo requerido (6-100). No se crea admin inicial.");
            return;
        }

        var existing = repository.findByCarnet(carnet);
        if (existing.isPresent()) {
            log.warn("AdminBootstrap: el carnet '{}' ya existe con rol {}. No se crea admin para evitar pisar.",
                    existing.get().getCarnet(), existing.get().getRol());
            return;
        }

        if (usingDefaultValues()) {
            log.warn("AdminBootstrap: usando credenciales por defecto (app.admin.*). Configurar variables de entorno en produccion.");
        }

        Usuario admin = Usuario.builder()
                .carnet(carnet)
                .nombre(nombre)
                .apellido(apellido)
                .correo(correo)
                .password(passwordEncoder.encode(password))
                .rol(Rol.ADMIN)
                .build();
        repository.save(admin);

        log.info("AdminBootstrap: admin inicial creado correctamente. carnet='{}', nombre='{} {}'",
                carnet, nombre, apellido);
    }

    private boolean usingDefaultValues() {
        return "ADMIN001".equals(carnet)
                && "admin123".equals(password)
                && "Admin".equals(nombre)
                && "Sistema".equals(apellido);
    }
}

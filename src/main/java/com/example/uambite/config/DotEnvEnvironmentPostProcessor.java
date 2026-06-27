package com.example.uambite.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Carga variables desde un archivo .env en el directorio de trabajo.
 * Se ejecuta antes que application.properties, por lo que las
 * variables definidas allí pueden usarse con la sintaxis ${VAR:default}.
 *
 * Formato esperado (línea por línea):
 *   # Comentario
 *   CLAVE=valor
 *   CLAVE="valor con espacios"
 *   CLAVE='valor con comillas'
 *
 * Las líneas vacías o que empiezan con # se ignoran.
 * Las variables de entorno del SO tienen prioridad sobre .env.
 */
public class DotEnvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String DOTENV_SOURCE_NAME = "dotenv";
    private static final String DOTENV_PATH_PROPERTY = "app.dotenv.path";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication application) {
        String path = environment.getProperty(DOTENV_PATH_PROPERTY, ".env");
        Resource resource = new FileSystemResource(path);
        if (!resource.exists()) {
            return;
        }
        Map<String, Object> properties = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                int eq = trimmed.indexOf('=');
                if (eq <= 0) {
                    continue;
                }
                String key = trimmed.substring(0, eq).trim();
                String value = trimmed.substring(eq + 1).trim();
                if ((value.startsWith("\"") && value.endsWith("\""))
                        || (value.startsWith("'") && value.endsWith("'"))) {
                    value = value.substring(1, value.length() - 1);
                }
                properties.put(key, value);
            }
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Error leyendo .env: " + e.getMessage(), e);
        }
        environment.getPropertySources()
                .addLast(new MapPropertySource(DOTENV_SOURCE_NAME, properties));
    }
}

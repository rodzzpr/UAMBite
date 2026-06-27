package com.example.uambite;

import com.example.uambite.config.DotEnvEnvironmentPostProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.PropertySource;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class DotEnvEnvironmentPostProcessorTest {

    @Test
    void cargaVariablesDesdeArchivoEnv(@TempDir Path tempDir) throws Exception {
        File dotenv = tempDir.resolve(".env").toFile();
        try (Writer w = new FileWriter(dotenv)) {
            w.write("# comentario ignorado\n");
            w.write("\n");
            w.write("DOTENV_TEST_VAR=valor_desde_archivo\n");
            w.write("DOTENV_TEST_QUOTED=\"valor entre comillas\"\n");
        }

        ConfigurableEnvironment env = new StandardEnvironment();
        System.setProperty("app.dotenv.path", dotenv.getAbsolutePath());
        try {
            new DotEnvEnvironmentPostProcessor()
                    .postProcessEnvironment(env, new SpringApplication(UAMBiteApplication.class));

            MapPropertySource source = (MapPropertySource) env.getPropertySources()
                    .get("dotenv");
            assertNotNull(source, "Debe existir la property source 'dotenv'");
            assertEquals("valor_desde_archivo", source.getProperty("DOTENV_TEST_VAR"));
            assertEquals("valor entre comillas", source.getProperty("DOTENV_TEST_QUOTED"));
        } finally {
            System.clearProperty("app.dotenv.path");
        }
    }

    @Test
    void noFallaSiNoExisteEnv(@TempDir Path tempDir) {
        ConfigurableEnvironment env = new StandardEnvironment();
        System.setProperty("app.dotenv.path", tempDir.resolve("no-existe.env").toString());
        try {
            assertDoesNotThrow(() ->
                    new DotEnvEnvironmentPostProcessor()
                            .postProcessEnvironment(env, new SpringApplication(UAMBiteApplication.class)));
            PropertySource<?> source = env.getPropertySources().get("dotenv");
            assertNull(source, "No debe crearse la source si no hay archivo");
        } finally {
            System.clearProperty("app.dotenv.path");
        }
    }
}

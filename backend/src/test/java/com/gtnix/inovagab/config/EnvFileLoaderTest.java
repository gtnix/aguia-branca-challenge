package com.gtnix.inovagab.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EnvFileLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    void carregaChaveAusenteDoArquivo() throws Exception {
        Path file = tempDir.resolve(".env");
        Files.writeString(file, "INOVAGAB_TEST_ONLY_KEY=from-file\n");
        System.clearProperty("INOVAGAB_TEST_ONLY_KEY");
        try {
            EnvFileLoader.load(file);
            assertEquals("from-file", System.getProperty("INOVAGAB_TEST_ONLY_KEY"));
        } finally {
            System.clearProperty("INOVAGAB_TEST_ONLY_KEY");
        }
        assertNull(System.getProperty("INOVAGAB_TEST_ONLY_KEY"));
    }
}

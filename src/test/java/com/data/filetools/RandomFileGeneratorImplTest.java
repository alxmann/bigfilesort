package com.data.filetools;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RandomFileGeneratorImplTest {

    private FileGenerator generator;
    private File file;

    @BeforeEach
    void setUp() {
        generator = FileToolsFactory.getFileGenerator("random");
    }

    @AfterEach
    void cleanUp() {
        if (file != null && file.exists()) {
            file.deleteOnExit();
        }
    }

    @Test
    void generatorProducesFileOfCorrectSize() throws IOException {
        long numberOfLines = 10L;
        int lineLength = 15;

        file = generator.generateStringContent(numberOfLines, lineLength);

        try (Stream<String> contentStream = Files.lines(file.toPath())) {
            long actualNumberOfLines = contentStream
                    .peek(line -> assertEquals(lineLength, line.length()))
                    .count();
            assertEquals(numberOfLines, actualNumberOfLines);
        }
    }
}
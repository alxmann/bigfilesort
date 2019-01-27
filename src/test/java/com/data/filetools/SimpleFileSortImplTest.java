package com.data.filetools;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleFileSortImplTest {

    private FileGenerator generator;
    private File file;
    private FileSort fileSort;
    private File sortedFile;

    @BeforeEach
    void setUp() {
        generator = FileToolsFactory.getFileGenerator("random");
        fileSort = FileToolsFactory.getFileSort("simple");
    }

    @AfterEach
    void cleanUp() {
        if (file != null && file.exists()) {
            file.deleteOnExit();
        }
        if (sortedFile != null && sortedFile.exists()) {
            sortedFile.deleteOnExit();
        }
    }

    @Test
    void fileSortProducesValidSortedFile() throws IOException {
        long numberOfLines = 100L;
        int lineLength = 80;

        file = generator.generateStringContent(numberOfLines, lineLength);
        Comparator<String> comparator = String::compareTo;
        sortedFile = fileSort.sortStringContent(file, comparator);

        // Verifying sorted file size.
        try (Stream<String> contentStream = Files.lines(sortedFile.toPath())) {
            long actualNumberOfLines = contentStream
                    .peek(line -> assertEquals(lineLength, line.length()))
                    .count();
            assertEquals(numberOfLines, actualNumberOfLines);
        }

        try (Stream<String> contentStream = Files.lines(sortedFile.toPath())) {
            Iterator<String> iterator = contentStream.iterator();
            String current;
            while (iterator.hasNext()) {
                current = iterator.next();
                if (iterator.hasNext()) {
                    assertThat(comparator.compare(iterator.next(), current), greaterThanOrEqualTo(0));
                }
            }
        }
    }
}
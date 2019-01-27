package com.data;

import com.data.filetools.FileGenerator;
import com.data.filetools.FileSort;
import com.data.filetools.FileToolsFactory;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.logging.Logger;

/**
 * External sort test application.
 */
public class App {
    private static final Logger LOG = Logger.getLogger(App.class.getName());
    private FileSort fileSort;
    private FileGenerator generator;

    /**
     * Creates test app instance with given file generator and sort algorithm.
     *
     * @param fileSort  Sort algorithm implementation.
     * @param generator File generator implementation.
     */
    public App(FileSort fileSort, FileGenerator generator) {
        this.fileSort = fileSort;
        this.generator = generator;
    }

    /**
     * Test app setting.
     *
     * @param args Not used.
     * @throws IOException In case of IO exception.
     */
    public static void main(String[] args) throws IOException {
        App app = new App(FileToolsFactory.getFileSort("simple"),
                FileToolsFactory.getFileGenerator("random"));
        app.run(10_000L, 150, String::compareTo);
    }

    /**
     * Generates file of given size and runs sorting algorithm.
     *
     * @param numberOfLines Number of lines in test file.
     * @param lineLength    Line length in test file.
     * @param comparator    Comparator for string order definition.
     * @throws IOException In case of IO exception.
     */
    public void run(long numberOfLines, int lineLength, Comparator<String> comparator) throws IOException {
        LOG.info("Performing sorting on file with " + numberOfLines + " lines of " + lineLength + " length.");

        long start = System.nanoTime();
        File testFile = generator.generateStringContent(numberOfLines, lineLength);
        long end = System.nanoTime();
        LOG.info("File generation took " + ((end - start) / 1000_000_000d) + " sec.");

        start = System.nanoTime();
        File sortedFile = fileSort.sortStringContent(testFile, comparator);
        end = System.nanoTime();
        LOG.info("Sorting took " + ((end - start) / 1000_000_000d) + " sec.");

        deleteFile(testFile);
        deleteFile(sortedFile);
    }

    private void deleteFile(File file) {
        if (file != null && file.exists()) {
            file.deleteOnExit();
        }
    }
}

package com.data.filetools;

/**
 * Factory for different file tools.
 */
public class FileToolsFactory {

    /**
     * Returns sort algorithm implementation corresponding input. Current options: [simple].
     *
     * @param fileSortType Type of sort algorithm.
     * @return {@link FileSort} instance or {@code null}.
     */
    public static FileSort getFileSort(String fileSortType) {
        if (fileSortType == null) return null;

        switch (fileSortType.toLowerCase()) {
            case "simple":
                return new SimpleFileSortImpl();
            default:
                return null;
        }
    }

    /**
     * Returns file generator implementation corresponding input. Current options: [random].
     *
     * @param generatorType Type of file generator.
     * @return {@link FileGenerator} instance or {@code null}.
     */
    public static FileGenerator getFileGenerator(String generatorType) {
        if (generatorType == null) return null;

        switch (generatorType.toLowerCase()) {
            case "random":
                return new RandomFileGeneratorImpl();
            default:
                return null;
        }
    }
}

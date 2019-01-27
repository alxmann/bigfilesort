package com.data.filetools;

import java.io.File;
import java.io.IOException;

/**
 * Helper for test files generation.
 */
public interface FileGenerator {

    /**
     * Generates test file with string content according given size parameters.
     *
     * @param numberOfLines Number of lines in generated file.
     * @param lineLength    Line length in generated file.
     * @return Generated file.
     * @throws IOException In case of IO exception.
     */
    File generateStringContent(long numberOfLines, int lineLength) throws IOException;
}

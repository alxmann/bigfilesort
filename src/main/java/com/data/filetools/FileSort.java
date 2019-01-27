package com.data.filetools;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;

/**
 * Helper to sort files which do not fit in memory.
 */
public interface FileSort {

    /**
     * Sorts files with string contents. Implementations guarantee sorting without OOM for files of any size.
     *
     * @param file       File to be sorted.
     * @param comparator Comparator for string order definition.
     * @return Sorted file.
     */
    File sortStringContent(File file, Comparator<String> comparator) throws IOException;
}

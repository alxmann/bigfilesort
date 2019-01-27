package com.data.filetools;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.logging.Logger;

/**
 * Simple algorithm to perform external sorting. Implementation uses {@code file.length() * 2} additional disc space for
 * temp files and defines temp file size based on memory available for JVM.
 */
class SimpleFileSortImpl implements FileSort {
    private static final Logger LOG = Logger.getLogger(SimpleFileSortImpl.class.getName());

    /**
     * {@inheritDoc}
     */
    public File sortStringContent(File file, Comparator<String> comparator) throws IOException {

        if (file.length() * 3 > file.getUsableSpace()) { // We don't want to go out of disk space.
            String message = "Required file size is too large to sort in current execution environment.";
            LOG.severe(message);
            throw new IllegalArgumentException(message);
        }

        List<File> chunks = null;
        try {
            chunks = splitAndSortTempFiles(file, comparator);
            return mergeSortedFiles(chunks, comparator);
        } catch (IOException e) {
            cleanUp(chunks);
            LOG.severe(e.getMessage());
            throw e;
        }
    }

    private List<File> splitAndSortTempFiles(File inputFile, Comparator<String> comparator)
            throws IOException {

        List<File> files = new ArrayList<>();

        try (RandomAccessFile raf = new RandomAccessFile(inputFile, "r")) {
            long sourceSize = raf.length();
            int numberOfChunks = defineNumberOfChunks(sourceSize);
            LOG.info("Sorting will perform on " + numberOfChunks + " chunks.");

            long bytesPerSplit = sourceSize / numberOfChunks;
            long remainingBytes = sourceSize % numberOfChunks;
            File chunkFile;

            for (int i = 0; i < numberOfChunks; i++) {
                chunkFile = readSortWriteToChunkFile(raf, bytesPerSplit, comparator);
                files.add(chunkFile);
            }
            if (remainingBytes > 0) {
                chunkFile = readSortWriteToChunkFile(raf, remainingBytes, comparator);
                files.add(chunkFile);
            }
        } catch (IOException e) {
            cleanUp(files);
            LOG.severe(e.getMessage());
            throw e;
        }
        return files;
    }

    private int defineNumberOfChunks(long sourceSize) {
        int numberOfChunks = (int) (sourceSize / (Runtime.getRuntime().freeMemory() * 0.8));
        if (numberOfChunks == 0) numberOfChunks = 1;
        return numberOfChunks;
    }

    private File readSortWriteToChunkFile(RandomAccessFile raf, long numBytes, Comparator<String> comparator)
            throws IOException {

        File chunkFile = null;
        BufferedWriter bw = null;
        try {
            chunkFile = File.createTempFile("test", ".tmp");
            PriorityQueue<String> lines = new PriorityQueue<>(comparator);
            long writtenBytes = 0;
            String line;
            while (writtenBytes < numBytes) {
                line = raf.readLine();
                if (line == null) break;
                lines.add(line);
                writtenBytes = line.length() * 2;
            }

            bw = Files.newBufferedWriter(chunkFile.toPath());
            while (!lines.isEmpty()) {
                bw.write(lines.poll());
                bw.newLine();
            }

        } catch (IOException e) {
            cleanUp(chunkFile);
            LOG.severe(e.getMessage());
            throw e;

        } finally {
            if (bw != null) bw.close();
        }
        return chunkFile;
    }

    private File mergeSortedFiles(List<File> files, Comparator<String> comparator) throws IOException {

        List<BufferedReader> brReaders = new ArrayList<>();
        PriorityQueue<Container> queue = new PriorityQueue<>();
        File outputFile = null;
        BufferedWriter bw = null;

        try {
            outputFile = File.createTempFile("test", ".tmp");
            bw = new BufferedWriter(new FileWriter(outputFile, true));
            for (File file : files) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                brReaders.add(br);
                String line = br.readLine();
                if (line == null) continue;
                queue.add(new Container(line, br, comparator));
            }
            while (!queue.isEmpty()) {
                Container nextToGo = queue.poll();
                bw.write(nextToGo.val);
                bw.newLine();

                String line = nextToGo.br.readLine();
                if (line != null) {
                    queue.add(new Container(line, nextToGo.br, comparator));
                }
            }

        } catch (IOException e) {
            cleanUp(outputFile);
            LOG.severe(e.getMessage());
            throw e;

        } finally {
            if (bw != null) {
                bw.close();
            }
            for (BufferedReader br : brReaders) {
                if (br != null) br.close();
            }
            cleanUp(files);
        }
        return outputFile;
    }

    private void cleanUp(List<File> files) {
        if (files != null) files.forEach(this::cleanUp);
    }

    private void cleanUp(File file) {
        if (file != null && file.exists()) file.deleteOnExit();
    }

    private class Container implements Comparable<Container> {
        String val;
        BufferedReader br;
        Comparator<String> comparator;

        Container(String val, BufferedReader br, Comparator<String> comparator) {
            this.val = val;
            this.br = br;
            this.comparator = comparator;
        }

        /**
         * Compares sting values of Containers using given comparator.
         *
         * @param o {@link Container} object to compare with.
         * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or
         * greater than the second.
         */
        @Override
        public int compareTo(Container o) {
            return comparator.compare(val, o.val);
        }
    }
}

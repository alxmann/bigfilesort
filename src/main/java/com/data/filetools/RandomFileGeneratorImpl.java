package com.data.filetools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.PrimitiveIterator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

/**
 * Random file generator implementation. Generates file content with string characters in [a-z] range. Allows to
 * create files not bigger than 1/3 of available disc space.
 */
class RandomFileGeneratorImpl implements FileGenerator {
    private static final Logger LOG = Logger.getLogger(RandomFileGeneratorImpl.class.getName());

    /**
     * {@inheritDoc}
     */
    @Override
    public File generateStringContent(long numberOfLines, int lineLength) throws IOException {
        if (lineLength < 0) {
            String message = "Line length is less then 0.";
            LOG.severe(message);
            throw new IllegalArgumentException(message);
        }

        File tempFile = File.createTempFile("test", ".tmp");

        long requiredBytes = (numberOfLines * lineLength) * 2; // Since 1 char is 2 bytes.
        if (requiredBytes > tempFile.getUsableSpace() / 3) { // We don't want to use more than 3d of our disk space.
            String message = "Required file size is too large for current execution environment.";
            LOG.severe(message);
            throw new IllegalArgumentException(message);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            PrimitiveIterator.OfInt characterStream = ThreadLocalRandom.current().ints('a', 'z' + 1).iterator();
            for (int i = 0; i < numberOfLines; i++) {
                for (int j = 0; j < lineLength; j++) {
                    writer.write(characterStream.nextInt());
                }
                writer.newLine();
            }
        } catch (IOException e) {
            if (tempFile.exists()) tempFile.deleteOnExit();
            throw e;
        }
        return tempFile;
    }
}

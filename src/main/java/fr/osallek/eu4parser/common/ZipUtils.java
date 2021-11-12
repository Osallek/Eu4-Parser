package fr.osallek.eu4parser.common;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class ZipUtils {

    private ZipUtils() {
    }

    public static Path newFile(Path destinationDir, ZipEntry zipEntry) throws IOException {
        Path destFile = destinationDir.resolve(zipEntry.getName());

        Path destDirPath = destinationDir.toFile().getCanonicalFile().toPath();
        Path destFilePath = destFile.toFile().getCanonicalFile().toPath();

        if (!destFilePath.startsWith(destDirPath)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    public static void unzip(Path zip, Path destination) throws IOException {
        try (ZipFile zipFile = new ZipFile(zip.toFile())) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();

                Path newFile = newFile(destination, zipEntry);
                if (zipEntry.isDirectory()) {
                    FileUtils.forceMkdir(newFile.toFile());
                } else {
                    // fix for Windows-created archives
                    FileUtils.forceMkdirParent(newFile.toFile());
                    FileUtils.copyToFile(zipFile.getInputStream(zipEntry), newFile.toFile());
                }
            }
        }
    }
}

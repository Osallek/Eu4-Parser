package fr.osallek.eu4parser.common;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public final class ZipUtils {

    private ZipUtils() {
    }

    public static void zipFolder(Path sourceFolderPath, Path zipPath, Predicate<Path> predicate) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()))) {
            Files.walkFileTree(sourceFolderPath, new SimpleFileVisitor<>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (!predicate.test(file)) {
                        return FileVisitResult.CONTINUE;
                    }

                    zos.putNextEntry(new ZipEntry(FilenameUtils.separatorsToUnix(sourceFolderPath.relativize(file).toString())));

                    try (InputStream stream = Files.newInputStream(file)) {
                        IOUtils.copy(stream, zos, 1_000_000);
                    }

                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
        }
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

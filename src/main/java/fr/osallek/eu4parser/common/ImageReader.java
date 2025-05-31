package fr.osallek.eu4parser.common;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.lang3.SystemUtils;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ImageReader {

    private ImageReader() {}

    public static BufferedImage convertFileToImage(File file) throws IOException {
        if (file == null || !file.exists()) {
            return null;
        }

        try {
            return ImageIO.read(file);
        } catch (IIOException e) {
            if (SystemUtils.IS_OS_WINDOWS && e.getCause() instanceof IllegalArgumentException e1 && e1.getMessage().startsWith("Unknown type:")
                && file.getName().toLowerCase().endsWith(".dds")) {
                File tempFile = PathUtils.getTempDirectory().resolve("texconv.exe").toAbsolutePath().normalize().toFile();
                if (!tempFile.exists()) {
                    tempFile.deleteOnExit();
                    try (InputStream in = ImageReader.class.getResourceAsStream("/bin/texconv.exe")) {
                        if (in == null) {
                            throw e;
                        }

                        Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                }

                try {
                    File pngFile = PathUtils.getTempDirectory().resolve(FilenameUtils.getBaseName(file.getName()) + ".png").toFile();
                    pngFile.deleteOnExit();
                    Process p = new ProcessBuilder(tempFile.getPath(), "-ft", "png", "-y", "-o", FileUtils.getTempDirectoryPath(),
                                                   file.getAbsolutePath()).start();
                    int code = p.waitFor();
                    if (code != 0) {
                        throw new RuntimeException(String.valueOf(code));
                    }

                    return ImageIO.read(pngFile);
                } catch (Exception e2) {
                    throw e;
                }
            } else {
                throw e;
            }
        }
    }
}

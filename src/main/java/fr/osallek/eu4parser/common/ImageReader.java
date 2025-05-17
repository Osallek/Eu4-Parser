package fr.osallek.eu4parser.common;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageReader {

    private ImageReader() {}

    public static BufferedImage convertFileToImage(File file) throws IOException {
        if (file == null || !file.exists()) {
            return null;
        }

        return ImageIO.read(file);
    }
}

package com.osallek.eu4parser;

import com.osallek.clausewitzparser.ClausewitzParser;
import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.eu4parser.common.Eu4Utils;
import com.osallek.eu4parser.model.save.Save;
import org.luaj.vm2.parser.ParseException;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Eu4Parser {

    private Eu4Parser() {}

    public static Save loadSave(String gameFolderPath, String path) throws IOException, ParseException {
        File file = new File(path);
        Save save = null;

        if (file.canRead()) {
            try {
                ZipFile zipFile = new ZipFile(path);
                save = new Save(file.getName(), gameFolderPath, ClausewitzParser.parse(zipFile, Eu4Utils.GAMESTATE_FILE, 1, ClausewitzUtils.CHARSET),
                                ClausewitzParser.parse(zipFile, Eu4Utils.AI_FILE, 1, ClausewitzUtils.CHARSET),
                                ClausewitzParser.parse(zipFile, Eu4Utils.META_FILE, 1, ClausewitzUtils.CHARSET));
            } catch (ZipException e) {
                save = new Save(file.getName(), gameFolderPath, ClausewitzParser.parse(file, 1, ClausewitzUtils.CHARSET));
            }
        }

        return save;
    }

    public static void writeSave(Save save, String path) throws IOException {
        if (save.isCompressed()) {
            try (ZipOutputStream outputStream = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(Paths.get(path))), ClausewitzUtils.CHARSET)) {
                outputStream.putNextEntry(new ZipEntry(Eu4Utils.AI_FILE));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, ClausewitzUtils.CHARSET));
                save.writeAi(writer);
                writer.flush();
                outputStream.closeEntry();

                outputStream.putNextEntry(new ZipEntry(Eu4Utils.GAMESTATE_FILE));
                writer = new BufferedWriter(new OutputStreamWriter(outputStream, ClausewitzUtils.CHARSET));
                save.writeGamestate(writer);
                writer.flush();
                outputStream.closeEntry();

                outputStream.putNextEntry(new ZipEntry(Eu4Utils.META_FILE));
                writer = new BufferedWriter(new OutputStreamWriter(outputStream, ClausewitzUtils.CHARSET));
                save.writeMeta(writer);
                writer.flush();
                outputStream.closeEntry();
            }
        } else {
            try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(path), ClausewitzUtils.CHARSET)) {
                save.writeAll(bufferedWriter);
            }
        }
    }
}

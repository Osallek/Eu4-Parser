package fr.osallek.eu4parser;

import fr.osallek.clausewitzparser.ClausewitzParser;
import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.model.ClausewitzObject;
import fr.osallek.clausewitzparser.model.ClausewitzPObject;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.save.Save;
import org.luaj.vm2.parser.ParseException;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Eu4Parser {

    private Eu4Parser() {}

    public static Save loadSave(String gameFolderPath, String modFolder, String path) throws IOException, ParseException {
        return loadSave(gameFolderPath, modFolder, path, new HashMap<>());
    }

    public static Save loadSave(String gameFolderPath, String modFolder, String path, Map<Predicate<ClausewitzItem>, Consumer<String>> listeners) throws IOException, ParseException {
        File file = new File(path);
        Save save = null;

        if (file.canRead()) {
            try (ZipFile zipFile = new ZipFile(path)) {
                save = new Save(file.getName(), gameFolderPath, modFolder,
                                ClausewitzParser.parse(zipFile, Eu4Utils.GAMESTATE_FILE, 1, ClausewitzUtils.CHARSET, listeners),
                                ClausewitzParser.parse(zipFile, Eu4Utils.AI_FILE, 1, ClausewitzUtils.CHARSET, listeners),
                                ClausewitzParser.parse(zipFile, Eu4Utils.META_FILE, 1, ClausewitzUtils.CHARSET, listeners));
            } catch (ZipException e) {
                save = new Save(file.getName(), gameFolderPath, modFolder, ClausewitzParser.parse(file, 1, ClausewitzUtils.CHARSET, listeners));
            }
        }

        return save;
    }

    public static List<String> getMods(String path) throws IOException {
        File file = new File(path);
        ClausewitzObject object;

        if (file.canRead()) {
            try (ZipFile zipFile = new ZipFile(path)) {
                object = ClausewitzParser.readSingleObject(zipFile, Eu4Utils.META_FILE, 1, ClausewitzUtils.CHARSET, "mod_enabled");
            } catch (ZipException e) {
                object = ClausewitzParser.readSingleObject(file, 1, ClausewitzUtils.CHARSET, "mod_enabled");
            }

            if (object != null && ClausewitzList.class.equals(object.getClass())) {
                return ((ClausewitzList) object).getValues();
            }
        }

        return new ArrayList<>();
    }

    public static Game parseGame(String gameFolderPath, String modFolderPath, List<String> modEnabled) throws IOException, ParseException {
        return new Game(gameFolderPath, modFolderPath, modEnabled);
    }

    public static void writeSave(Save save, String path) throws IOException {
        writeSave(save, path, new HashMap<>());
    }

    public static void writeSave(Save save, String path, Map<Predicate<ClausewitzPObject>, Consumer<String>> listeners) throws IOException {
        if (save.isCompressed()) {
            try (ZipOutputStream outputStream = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(Paths.get(path))),
                                                                    ClausewitzUtils.CHARSET)) {
                outputStream.putNextEntry(new ZipEntry(Eu4Utils.AI_FILE));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, ClausewitzUtils.CHARSET));
                save.writeAi(writer, listeners);
                writer.flush();
                outputStream.closeEntry();

                outputStream.putNextEntry(new ZipEntry(Eu4Utils.GAMESTATE_FILE));
                writer = new BufferedWriter(new OutputStreamWriter(outputStream, ClausewitzUtils.CHARSET));
                save.writeGamestate(writer, listeners);
                writer.flush();
                outputStream.closeEntry();

                outputStream.putNextEntry(new ZipEntry(Eu4Utils.META_FILE));
                writer = new BufferedWriter(new OutputStreamWriter(outputStream, ClausewitzUtils.CHARSET));
                save.writeMeta(writer, listeners);
                writer.flush();
                outputStream.closeEntry();
            }
        } else {
            try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(path), ClausewitzUtils.CHARSET)) {
                save.writeAll(bufferedWriter, listeners);
            }
        }
    }
}

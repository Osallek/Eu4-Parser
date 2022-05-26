package fr.osallek.eu4parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.model.ClausewitzObject;
import fr.osallek.clausewitzparser.model.ClausewitzPObject;
import fr.osallek.clausewitzparser.parser.ClausewitzParser;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.save.Save;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
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

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static LauncherSettings loadSettings(String gameFolderPath) throws IOException {
        return OBJECT_MAPPER.readValue(Paths.get(gameFolderPath).resolve("launcher-settings.json").toFile(), LauncherSettings.class);
    }

    public static Save loadSave(String gameFolderPath, String modFolder, String path) throws IOException {
        return loadSave(gameFolderPath, modFolder, path, new HashMap<>());
    }

    public static Save loadSave(String gameFolderPath, String modFolder, String path,
                                Map<Predicate<ClausewitzPObject>, Consumer<String>> listeners) throws IOException {
        File file = new File(path);
        Save save = null;

        if (file.canRead()) {
            try (ZipFile zipFile = new ZipFile(path)) {
                save = new Save(file.getName(), gameFolderPath, modFolder,
                                ClausewitzParser.parse(zipFile, Eu4Utils.GAMESTATE_FILE, 1, listeners),
                                ClausewitzParser.parse(zipFile, Eu4Utils.AI_FILE, 1, listeners),
                                ClausewitzParser.parse(zipFile, Eu4Utils.META_FILE, 1, listeners));
            } catch (ZipException e) {
                save = new Save(file.getName(), gameFolderPath, modFolder, ClausewitzParser.parse(file, 1, listeners));
            }
        }

        return save;
    }

    public static List<String> getMods(String path) throws IOException {
        File file = new File(path);
        ClausewitzObject object;

        if (file.canRead()) {
            try (ZipFile zipFile = new ZipFile(path)) {
                object = ClausewitzParser.readSingleObject(zipFile, Eu4Utils.META_FILE, 1, "mod_enabled");
            } catch (ZipException e) {
                object = ClausewitzParser.readSingleObject(file, 1, "mod_enabled");
            }

            if (object != null && ClausewitzList.class.equals(object.getClass())) {
                return ((ClausewitzList) object).getValues();
            }
        }

        return new ArrayList<>();
    }

    public static Game parseGame(String gameFolderPath) throws IOException {
        return new Game(gameFolderPath);
    }

    public static Game parseGame(String gameFolderPath, LauncherSettings launcherSettings) throws IOException {
        return new Game(gameFolderPath, launcherSettings);
    }

    public static Game parseGame(String gameFolderPath, List<String> modEnabled) throws IOException {
        return new Game(gameFolderPath, modEnabled);
    }

    public static Game parseGame(String gameFolderPath, List<String> modEnabled, LauncherSettings launcherSettings) throws IOException {
        return new Game(gameFolderPath, launcherSettings, modEnabled);
    }

    public static Game parseGame(String gameFolderPath, List<String> modEnabled, Runnable runnable) throws IOException {
        return new Game(gameFolderPath, modEnabled, runnable);
    }

    public static Game parseGame(String gameFolderPath, List<String> modEnabled, Runnable runnable, LauncherSettings launcherSettings) throws IOException {
        return new Game(gameFolderPath, launcherSettings, modEnabled, runnable);
    }

    public static void writeSave(Save save, String path) throws IOException {
        writeSave(save, path, new HashMap<>());
    }

    public static void writeSave(Save save, String path, Map<Predicate<ClausewitzPObject>, Consumer<String>> listeners) throws IOException {
        if (save.isCompressed()) {
            try (ZipOutputStream outputStream = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(Paths.get(path))),
                                                                    StandardCharsets.ISO_8859_1)) {
                outputStream.putNextEntry(new ZipEntry(Eu4Utils.AI_FILE));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.ISO_8859_1));
                save.writeAi(writer, listeners);
                writer.flush();
                outputStream.closeEntry();

                outputStream.putNextEntry(new ZipEntry(Eu4Utils.GAMESTATE_FILE));
                writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.ISO_8859_1));
                save.writeGamestate(writer, listeners);
                writer.flush();
                outputStream.closeEntry();

                outputStream.putNextEntry(new ZipEntry(Eu4Utils.META_FILE));
                writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.ISO_8859_1));
                save.writeMeta(writer, listeners);
                writer.flush();
                outputStream.closeEntry();
            }
        } else {
            try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(path), StandardCharsets.ISO_8859_1)) {
                save.writeAll(bufferedWriter, listeners);
            }
        }
    }
}

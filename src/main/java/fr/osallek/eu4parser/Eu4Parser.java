package fr.osallek.eu4parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzObject;
import fr.osallek.clausewitzparser.model.ClausewitzPObject;
import fr.osallek.clausewitzparser.parser.ClausewitzParser;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.LauncherSettings;
import fr.osallek.eu4parser.model.Mod;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.save.Save;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArchUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Eu4Parser {

    private Eu4Parser() {}

    private static final Logger LOGGER = LoggerFactory.getLogger(Eu4Parser.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static final Charset SAVE_CHARSET = Charset.forName("Windows-1252");

    public static LauncherSettings loadSettings(Path gameFolderPath) throws IOException {
        LauncherSettings launcherSettings = OBJECT_MAPPER.readValue(gameFolderPath.resolve("launcher-settings.json").toFile(), LauncherSettings.class);
        launcherSettings.setGameFolderPath(gameFolderPath);

        return launcherSettings;
    }

    public static Optional<Path> detectSteamFolder() throws IOException, InterruptedException {
        if (SystemUtils.IS_OS_WINDOWS) {
            if (ArchUtils.getProcessor().is64Bit()) {
                return Eu4Utils.readRegistry("HKEY_LOCAL_MACHINE\\SOFTWARE\\Wow6432Node\\Valve\\Steam", "InstallPath").map(Path::of);
            } else {
                return Eu4Utils.readRegistry("HKEY_LOCAL_MACHINE\\SOFTWARE\\Valve\\Steam", "InstallPath").map(Path::of);
            }
        } else if (SystemUtils.IS_OS_LINUX) {
            Path steamPath = Path.of(System.getProperty("user.home"), ".steam", "steam");

            if (Files.exists(steamPath) && Files.exists(steamPath.toRealPath()) && Files.isDirectory(steamPath.toRealPath())) {
                return Optional.of(steamPath.toRealPath());
            }
        }

        return Optional.empty();
    }

    public static Optional<Path> detectInstallationFolder() {

        try {
            Optional<Path> steamFolder = detectSteamFolder();

            if (steamFolder.isPresent()) {
                Path path = steamFolder.get().resolve(Eu4Utils.STEAM_APPS_FOLDER_PATH);

                if (Files.exists(path) && Files.isDirectory(path)) {
                    Path manifestPath = path.resolve("appmanifest_236850.acf");

                    if (Files.exists(manifestPath) && Files.isRegularFile(manifestPath)) {
                        return Eu4Utils.readManifest(manifestPath);
                    }

                    Path libraryFoldersPath = path.resolve("libraryfolders.vdf");

                    if (Files.exists(libraryFoldersPath) && Files.isRegularFile(libraryFoldersPath)) {
                        List<String> lines = Files.readAllLines(libraryFoldersPath);

                        List<String> paths = lines.stream().filter(s -> s.contains("\"path\"")).toList();
                        if (CollectionUtils.isNotEmpty(paths)) {
                            for (String s : paths) {
                                String folder = s.replace("\"path\"", "").trim();
                                folder = folder.substring(1, folder.length() - 1);

                                Path libraryFolder = Path.of(folder).resolve(Eu4Utils.STEAM_APPS_FOLDER_PATH);
                                manifestPath = libraryFolder.resolve("appmanifest_236850.acf");

                                if (Files.exists(manifestPath) && Files.isRegularFile(manifestPath)) {
                                    return Eu4Utils.readManifest(manifestPath);
                                }
                            }
                        }
                    }

                }
            }
        } catch (InterruptedException e) {
            LOGGER.error("An error occurred while searching for game installation folder: {}!", e.getMessage(), e);
            Thread.currentThread().interrupt();
            return Optional.empty();
        } catch (Exception e) {
            LOGGER.error("An error occurred while searching for game installation folder: {}!", e.getMessage(), e);
            return Optional.empty();
        }

        Path path = Path.of("C:", "Program Files (x86)", "Steam", "steamapps", "common", "Europa Universalis IV");

        if (Files.exists(path) && Files.isDirectory(path)) {
            return Optional.of(path);
        }

        return Optional.empty();
    }

    public static List<Mod> detectMods(LauncherSettings launcherSettings) {
        if (launcherSettings.getModFolder().toFile().exists() && launcherSettings.getModFolder().toFile().isDirectory()) {
            try (Stream<Path> stream = Files.list(launcherSettings.getModFolder())) {
                return stream.filter(path -> path.getFileName().toString().endsWith(".mod"))
                             .map(path -> new Mod(path.toFile(), ClausewitzParser.parse(path.toFile(), 0), launcherSettings))
                             .sorted(Comparator.comparing(Mod::getName, Eu4Utils.COLLATOR))
                             .toList();
            } catch (Exception e) {
                LOGGER.error("An error occurred while searching for mods: {}!", e.getMessage(), e);
            }
        }

        return new ArrayList<>();
    }

    public static boolean isIronman(Path path) throws IOException {
        File file = path.toFile();

        if (file.canRead()) {
            try (ZipFile zipFile = new ZipFile(file)) {
                ZipEntry zipEntry = zipFile.getEntry(Eu4Utils.META_FILE);

                try (InputStream stream = zipFile.getInputStream(zipEntry);
                     InputStreamReader inputStreamReader = new InputStreamReader(stream, SAVE_CHARSET);
                     BufferedReader reader = new BufferedReader(inputStreamReader)) {
                    return !Eu4Utils.MAGIC_WORD.equals(reader.readLine());
                }
            } catch (ZipException e) {
                try (BufferedReader reader = Files.newBufferedReader(file.toPath(), SAVE_CHARSET)) {
                    return !Eu4Utils.MAGIC_WORD.equals(reader.readLine());
                }
            }
        } else {
            return true;
        }
    }

    public static Save loadSave(Path gameFolderPath, Path path) throws IOException {
        return loadSave(gameFolderPath, path, loadSettings(gameFolderPath), new HashMap<>());
    }

    public static Save loadSave(Path gameFolderPath, Path path, LauncherSettings launcherSettings) throws IOException {
        return loadSave(gameFolderPath, path, launcherSettings, new HashMap<>());
    }

    public static Save loadSave(Path gameFolderPath, Path path, LauncherSettings launcherSettings,
                                Map<Predicate<ClausewitzPObject>, Consumer<String>> listeners) throws IOException {
        File file = path.toFile();
        Save save = null;

        if (file.canRead()) {
            try (ZipFile zipFile = new ZipFile(file)) {
                save = new Save(file.getName(), gameFolderPath,
                                ClausewitzParser.parse(zipFile, Eu4Utils.GAMESTATE_FILE, 1, listeners, SAVE_CHARSET),
                                ClausewitzParser.parse(zipFile, Eu4Utils.AI_FILE, 1, listeners, SAVE_CHARSET),
                                ClausewitzParser.parse(zipFile, Eu4Utils.META_FILE, 1, listeners, SAVE_CHARSET),
                                launcherSettings);
            } catch (ZipException e) {
                save = new Save(file.getName(), gameFolderPath, ClausewitzParser.parse(file, 1, listeners, SAVE_CHARSET), launcherSettings);
            }
        }

        return save;
    }

    public static Save loadSave(Path path, Game game, Map<Predicate<ClausewitzPObject>, Consumer<String>> listeners) throws IOException {
        File file = path.toFile();
        Save save = null;

        if (file.canRead()) {
            try (ZipFile zipFile = new ZipFile(file)) {
                save = new Save(file.getName(),
                                ClausewitzParser.parse(zipFile, Eu4Utils.GAMESTATE_FILE, 1, listeners, SAVE_CHARSET),
                                ClausewitzParser.parse(zipFile, Eu4Utils.AI_FILE, 1, listeners, SAVE_CHARSET),
                                ClausewitzParser.parse(zipFile, Eu4Utils.META_FILE, 1, listeners, SAVE_CHARSET),
                                true,
                                game);
            } catch (ZipException e) {
                save = new Save(file.getName(), ClausewitzParser.parse(file, 1, listeners, SAVE_CHARSET), game);
            }
        }

        return save;
    }

    public static List<String> getMods(Path path) throws IOException {
        ClausewitzObject object;

        if (path.toFile().canRead()) {
            try (ZipFile zipFile = new ZipFile(path.toFile())) {
                object = ClausewitzParser.readSingleObject(zipFile, Eu4Utils.META_FILE, 1, "mods_enabled_names");
            } catch (ZipException e) {
                object = ClausewitzParser.readSingleObject(path.toFile(), 1, "mods_enabled_names");
            }

            if (object != null && ClausewitzItem.class.equals(object.getClass())) {
                return ((ClausewitzItem) object).getChildren().stream().map(item -> item.getVarAsString("filename")).filter(Objects::nonNull).toList();
            }
        }

        return new ArrayList<>();
    }

    public static Game parseGame(Path gameFolderPath) throws IOException {
        return new Game(gameFolderPath);
    }

    public static Game parseGame(Path gameFolderPath, LauncherSettings launcherSettings) throws IOException {
        return new Game(gameFolderPath, launcherSettings);
    }

    public static Game parseGame(Path gameFolderPath, List<String> modEnabled) throws IOException {
        return new Game(gameFolderPath, modEnabled);
    }

    public static Game parseGame(Path gameFolderPath, List<String> modEnabled, LauncherSettings launcherSettings) throws IOException {
        return new Game(gameFolderPath, launcherSettings, modEnabled);
    }

    public static Game parseGame(Path gameFolderPath, List<String> modEnabled, Runnable runnable) throws IOException {
        return new Game(gameFolderPath, modEnabled, runnable);
    }

    public static Game parseGame(Path gameFolderPath, List<String> modEnabled, LauncherSettings launcherSettings, Runnable runnable) throws IOException {
        return new Game(gameFolderPath, launcherSettings, modEnabled, runnable);
    }

    public static void writeSave(Save save, Path path) throws IOException {
        writeSave(save, path, new HashMap<>());
    }

    public static void writeSave(Save save, Path path, Map<Predicate<ClausewitzPObject>, Consumer<String>> listeners) throws IOException {
        if (save.isCompressed()) {
            try (ZipOutputStream outputStream = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(path)),
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
            try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.ISO_8859_1)) {
                save.writeAll(bufferedWriter, listeners);
            }
        }
    }
}

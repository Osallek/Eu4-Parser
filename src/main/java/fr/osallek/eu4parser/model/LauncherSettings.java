package fr.osallek.eu4parser.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.game.localisation.Eu4Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LauncherSettings {

    private static final Logger LOGGER = LoggerFactory.getLogger(LauncherSettings.class);

    private Path gameFolderPath;

    private final String browserDlcUrl;

    private final String distPlatform;

    private final List<String> exeArgs;

    private final String exePath;

    private final Path gameDataPath;

    private final String gameId;

    private final String ingameSettingsLayoutPath;

    private final String rawVersion;

    private final String themeFile;

    private final String version;

    private final Eu4Language gameLanguage;

    private final Path modFolder;

    private final Path savesFolder;

    @JsonCreator
    public LauncherSettings(@JsonProperty("browserDlcUrl") String browserDlcUrl, @JsonProperty("distPlatform") String distPlatform,
                            @JsonProperty("exeArgs") List<String> exeArgs, @JsonProperty("exePath") String exePath,
                            @JsonProperty("gameDataPath") String gameDataPath, @JsonProperty("gameId") String gameId,
                            @JsonProperty("ingameSettingsLayoutPath") String ingameSettingsLayoutPath, @JsonProperty("rawVersion") String rawVersion,
                            @JsonProperty("themeFile") String themeFile, @JsonProperty("version") String version) {
        this.browserDlcUrl = browserDlcUrl;
        this.distPlatform = distPlatform;
        this.exeArgs = exeArgs;
        this.exePath = exePath;
        this.gameDataPath = Path.of(gameDataPath.replace("%USER_DOCUMENTS%", Eu4Utils.DOCUMENTS_FOLDER.toString()));
        this.gameId = gameId;
        this.ingameSettingsLayoutPath = ingameSettingsLayoutPath;
        this.rawVersion = rawVersion;
        this.themeFile = themeFile;
        this.version = version;

        Eu4Language gameLanguage1;
        Path path = this.gameDataPath.resolve("settings.txt");
        try (Stream<String> lines = Files.lines(path)) {
            gameLanguage1 = lines.filter(line -> line.startsWith("language"))
                                 .findFirst()
                                 .map(s -> s.replace("language=", ""))
                                 .map(ClausewitzUtils::removeQuotes)
                                 .map(Eu4Language::getByEndWith)
                                 .orElse(Eu4Language.getDefault());
        } catch (IOException e) {
            LOGGER.error("{}", e.getMessage(), e);
            gameLanguage1 = Eu4Language.getDefault();
        }

        this.gameLanguage = gameLanguage1;
        this.modFolder = this.gameDataPath.resolve("mod");
        this.savesFolder = this.gameDataPath.resolve(Eu4Utils.SAVES_FOLDER);
    }

    public Path getGameFolderPath() {
        return gameFolderPath;
    }

    public void setGameFolderPath(Path gameFolderPath) {
        this.gameFolderPath = gameFolderPath;
    }

    public String getBrowserDlcUrl() {
        return browserDlcUrl;
    }

    public String getDistPlatform() {
        return distPlatform;
    }

    public List<String> getExeArgs() {
        return exeArgs;
    }

    public String getExePath() {
        return exePath;
    }

    public Path getGameDataPath() {
        return gameDataPath;
    }

    public String getGameId() {
        return gameId;
    }

    public String getIngameSettingsLayoutPath() {
        return ingameSettingsLayoutPath;
    }

    public String getRawVersion() {
        return rawVersion;
    }

    public String getThemeFile() {
        return themeFile;
    }

    public String getVersion() {
        return version;
    }

    public Eu4Language getGameLanguage() {
        return gameLanguage;
    }

    public Path getModFolder() {
        return modFolder;
    }

    public Path getSavesFolder() {
        return savesFolder;
    }
}

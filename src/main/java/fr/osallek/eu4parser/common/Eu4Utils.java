package fr.osallek.eu4parser.common;

import com.googlecode.pngtastic.core.PngImage;
import com.googlecode.pngtastic.core.PngOptimizer;
import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.eu4parser.model.game.Building;
import fr.osallek.eu4parser.model.game.Country;
import org.apache.commons.collections4.iterators.ReverseListIterator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.filechooser.FileSystemView;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.text.Collator;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class Eu4Utils {

    private Eu4Utils() {}

    private static final Logger LOGGER = LoggerFactory.getLogger(Eu4Utils.class);

    public static final ThreadPoolExecutor POOL_EXECUTOR = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    public static final Collator COLLATOR = Collator.getInstance();

    public static final Path DOCUMENTS_FOLDER = FileSystemView.getFileSystemView().getDefaultDirectory().toPath().toAbsolutePath();

    public static final Path OSALLEK_DOCUMENTS_FOLDER = DOCUMENTS_FOLDER.resolve("Osallek");

    public static final String DESCRIPTOR_FILE = "descriptor.mod";

    public static final String SAVES_FOLDER = "save games";


    public static final String STEAM_COMMON_FOLDER_PATH = "common";

    public static final String STEAM_APPS_FOLDER_PATH = "steamapps";

    public static final PathMatcher TXT_PATH_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**.txt");

    public static final PathMatcher LUA_PATH_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**.lua");

    public static final String MAGIC_WORD = "EU4txt";
    public static final String IRONMAN_MAGIC_WORD = "EU4bin";

    public static final String AI_FILE = "ai";

    public static final String GAMESTATE_FILE = "gamestate";

    public static final String META_FILE = "meta";

    public static final String MAP_FOLDER_PATH = "map";

    public static final String COMMON_FOLDER_PATH = "common";

    public static final String HISTORY_FOLDER_PATH = "history";

    public static final String GFX_FOLDER_PATH = "gfx";

    public static final String LOCALISATION_FOLDER_PATH = "localisation";

    public static final String INTERFACE_FOLDER_PATH = "interface";

    public static final String MISSIONS_FOLDER_PATH = "missions";

    public static final String ADVISORS_FOLDER_PATH = "advisors";

    public static final String BUILDINGS_FOLDER_PATH = "buildings";

    public static final String GRAPHICAL_CULTURES_FILE = "graphicalculturetype.txt";

    public static final LocalDate DEFAULT_DATE = LocalDate.of(1, 1, 1);

    public static final String DEFAULT_TAG = "---";

    public static final String DEFAULT_TAG_QUOTES = "\"" + DEFAULT_TAG + "\"";

    public static final String IMPASSABLE_CLIMATE = "impassable";

    public static final String IMPASSABLE_LOCALIZATION = "WASTELAND";

    public static final String DEFAULT_CLIMATE = "temperate";

    public static final String DEFAULT_MONSOON = "no_monsoon";

    public static final String DEFAULT_WINTER = "NO_WINTER";

    public static final String DEFINE_KEY = "NDefines";

    public static final String DEFINE_GAME_KEY = "NGame";

    public static final String DEFINE_DIPLOMACY_KEY = "NDiplomacy";

    public static final String DEFINE_COUNTRY_KEY = "NCountry";

    public static final String DEFINE_ECONOMY_KEY = "NEconomy";

    public static final String DEFINE_MILITARY_KEY = "NMilitary";

    public static final String DEFINE_AI_KEY = "NAI";

    public static final String DEFINE_AI_ECONOMY_KEY = "NAIEconomy";

    public static final String DEFINE_GRAPHICS_KEY = "NGraphics";

    public static final String DEFINE_GUI_KEY = "NGui";

    public static final String DEFINE_ENGINE_KEY = "NEngine";

    public static final String DEFINE_MACRO_BUILD_COLORS_KEY = "NMacroBuildColors";

    public static final String DEFINE_FRONTEND_KEY = "NFrontend";

    public static final String DEFINE_RELIGION_KEY = "NReligion";

    public static final String DEFINE_NATION_DESIGNER_KEY = "NNationDesigner";

    public static final String DEFINE_GOVERNMENT_KEY = "NGovernment";

    public static final String SUBJECT_TYPE_COLONY = "colony";

    public static final String SUBJECT_TYPE_MARCH = "march";

    public static final String SUBJECT_TYPE_VASSAL = "vassal";

    public static final String SUBJECT_TYPE_DAIMYO_VASSAL = "daimyo_vassal";

    public static final String SUBJECT_TYPE_TRIBUTARY_STATE = "tributary_state";

    public static final String SUBJECT_TYPE_CLIENT_VASSAL = "client_vassal";

    public static final String SUBJECT_TYPE_PERSONAL_UNION = "personal_union";

    public static final Pattern MOD_FILE_NAME_PATTERN = Pattern.compile("ugc_[0-9]+.mod");

    public static final Pattern DATE_PATTERN = Pattern.compile("-?[0-9]{1,4}.[0-9]{1,2}.[0-9]{1,2}");

    public static final List<String> HISTORICAL_COUNCILS = List.of("concilatory", "neutral", "harsh");

    public static final Path FLAGS_GFX = Path.of(GFX_FOLDER_PATH, "flags");

    public static final Path MISSIONS_GFX = Path.of(GFX_FOLDER_PATH, INTERFACE_FOLDER_PATH, MISSIONS_FOLDER_PATH);

    public static final Path ADVISORS_GFX = Path.of(GFX_FOLDER_PATH, INTERFACE_FOLDER_PATH, ADVISORS_FOLDER_PATH);

    public static final Path BUILDINGS_GFX = Path.of(GFX_FOLDER_PATH, INTERFACE_FOLDER_PATH, BUILDINGS_FOLDER_PATH);

    public static final PngOptimizer PNG_OPTIMIZER = new PngOptimizer();

    static {
        COLLATOR.setStrength(Collator.NO_DECOMPOSITION);
    }

    public static synchronized void optimizePng(Path file, Path dest) throws IOException {
        try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file.toFile().getAbsolutePath()))) {
            PngImage pngImage = PNG_OPTIMIZER.optimize(new PngImage(stream, null), false, 9);
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                pngImage.writeDataOutputStream(outputStream);
                pngImage.export(dest.toFile().getAbsolutePath(), outputStream.toByteArray());
            }
        }
    }

    public static Optional<String> readRegistry(String location, String key) throws InterruptedException, IOException {
        Process process = new ProcessBuilder("reg", "query", "\"" + location + "\"", "/v", "\"" + key + "\"").start();
        process.waitFor();

        String text = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        if (StringUtils.isNotBlank(text)) {
            if (text.contains("\t")) {
                return Optional.of(text.substring(text.lastIndexOf("\t")).trim());
            } else if (text.contains("    ")) {
                return Optional.of(text.substring(text.lastIndexOf("    ")).trim());
            }
        }

        return Optional.empty();
    }

    public static Optional<Path> readManifest(Path manifestPath) throws IOException {
        if (Files.exists(manifestPath) && Files.isRegularFile(manifestPath)) {
            List<String> lines = Files.readAllLines(manifestPath);

            Optional<String> line = lines.stream().filter(s -> s.contains("\"installdir\"")).findFirst();
            if (line.isPresent()) {
                String folder = line.get().replace("\"installdir\"", "").trim();
                folder = folder.substring(1, folder.length() - 1);
                Path path = manifestPath.getParent().resolve(STEAM_COMMON_FOLDER_PATH).resolve(folder);

                if (Files.exists(path) && Files.isDirectory(path)) {
                    return Optional.of(path);
                }
            }
        }

        return Optional.empty();
    }

    public static boolean isTag(String s) {
        return Country.CUSTOM_COUNTRY_PATTERN.matcher(s).matches() || Country.COLONY_PATTERN.matcher(s).matches()
               || Country.TRADING_CITY_PATTERN.matcher(s).matches() || Country.CLIENT_STATE_PATTERN.matcher(s).matches()
               || Country.COUNTRY_PATTERN.matcher(s).matches();
    }

    public static LocalDate stringToDate(String s) {
        try {
            return ClausewitzUtils.stringToDate(s);
        } catch (DateTimeException e) {
            return null;
        }
    }

    public static Integer cleanStringAndParseToInt(String s) {
        return Integer.parseInt(s.replaceAll("[\\D]", ""));
    }

    public static Double cleanStringAndParseToDouble(String s) {
        return Double.parseDouble(s.replaceAll("[\\D.]", ""));
    }

    @SafeVarargs
    public static <K, V> Map<K, V> mergeMaps(Map<K, V>... maps) {
        Map<K, V> map = null;

        for (Map<K, V> kvMap : maps) {
            if (kvMap != null) {
                if (map == null) {
                    map = kvMap;
                } else {
                    map.putAll(kvMap);
                }
            }
        }

        return map;
    }

    public static List<List<Building>> buildingsTree(List<Building> buildings) {
        List<Building> queue = new ArrayList<>(buildings);
        List<List<Building>> tree = new ArrayList<>();
        List<Building> manufactories = new ArrayList<>();

        Iterator<Building> iterator = queue.iterator();
        while (iterator.hasNext()) {
            Building building = iterator.next();
            if (!building.getManufactoryFor().isEmpty()) {
                manufactories.add(building);
                iterator.remove();
            } else if (ClausewitzUtils.isBlank(building.getMakeObsolete())) {
                List<Building> buildingList = new ArrayList<>();
                buildingList.add(building);
                tree.add(buildingList);
                iterator.remove();
            }
        }

        while (!queue.isEmpty()) {
            int size = queue.size();
            iterator = new ReverseListIterator<>(queue); //Reverse iterator, so if we have multiple buildings replacing the same, we keep the last as the game
            while (iterator.hasNext()) {
                Building building = iterator.next();
                for (List<Building> buildingList : tree) {
                    if (buildingList.get(buildingList.size() - 1).getName().equals(building.getMakeObsolete())) {
                        buildingList.add(building);
                        iterator.remove();
                    }
                }
            }

            if (queue.size() == size) { //Did not remove an building, meaning weird behaviour (multiple buildings replacing the same)
                queue.clear();
            }
        }

        tree.add(manufactories);

        return tree;
    }

    public static int rgbToColor(int red, int green, int blue) {
        return ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | ((blue & 0xFF));
    }

    public static boolean isRegularTxtFile(File file) {
        return isRegularTxtFile(file.toPath());
    }

    public static boolean isRegularTxtFile(Path path) {
        return Files.isRegularFile(path) && TXT_PATH_MATCHER.matches(path);
    }

    public static boolean isRegularLuaFile(File file) {
        return isRegularLuaFile(file.toPath());
    }

    public static boolean isRegularLuaFile(Path path) {
        return Files.isRegularFile(path) && LUA_PATH_MATCHER.matches(path);
    }

    @SafeVarargs
    public static <T, R> R coalesce(T value, Function<T, R>... functions) {
        for (Function<T, R> f : functions) {
            R apply = f.apply(value);

            if (apply != null) {
                return apply;
            }
        }

        return null;
    }

    @SafeVarargs
    public static <T> T coalesce(T... items) {
        return Arrays.stream(items).filter(Objects::nonNull).findFirst().orElse(null);
    }

    public static List<String> modifierToLocalisationKeys(String s) {
        List<String> keys = new ArrayList<>();

        if (s.startsWith("tech_")) {
            s = s.substring(5);
        }

        keys.add("modifier_" + s);
        keys.add(("modifier_" + s).toLowerCase());
        keys.add(("modifier_" + s).toUpperCase());
        keys.add(s);
        keys.add(s.toLowerCase());
        keys.add(s.toUpperCase());
        keys.add("idea_" + s);
        keys.add(("idea_" + s).toLowerCase());
        keys.add(("idea_" + s).toUpperCase());

        if (s.endsWith("_modifier") || s.endsWith("_MODIFIER")) {
            s = s.substring(0, s.length() - 9);
            keys.add("modifier_" + s);
            keys.add(("modifier_" + s).toLowerCase());
            keys.add(("modifier_" + s).toUpperCase());
            keys.add(s);
            keys.add(s.toLowerCase());
            keys.add(s.toUpperCase());
            keys.add("idea_" + s);
            keys.add(("idea_" + s).toLowerCase());
            keys.add(("idea_" + s).toUpperCase());
        }

        if (s.endsWith("_modifer") || s.endsWith("_MODIFER")) {
            s = s.substring(0, s.length() - 8);
            keys.add("modifier_" + s);
            keys.add(("modifier_" + s).toLowerCase());
            keys.add(("modifier_" + s).toUpperCase());
            keys.add(s);
            keys.add(s.toLowerCase());
            keys.add(s.toUpperCase());
            keys.add("idea_" + s);
            keys.add(("idea_" + s).toLowerCase());
            keys.add(("idea_" + s).toUpperCase());
        }

        if (s.startsWith("num_") || s.startsWith("NUM_")) {
            s = s.substring(4);
            keys.add("modifier_" + s);
            keys.add(("modifier_" + s).toLowerCase());
            keys.add(("modifier_" + s).toUpperCase());
            keys.add(s);
            keys.add(s.toLowerCase());
            keys.add(s.toUpperCase());
            keys.add("idea_" + s);
            keys.add(("idea_" + s).toLowerCase());
            keys.add(("idea_" + s).toUpperCase());
        }

        if (s.startsWith("global_") || s.startsWith("GLOBAL_")) {
            s = s.substring(7);
            keys.add("modifier_" + s);
            keys.add(("modifier_" + s).toLowerCase());
            keys.add(("modifier_" + s).toUpperCase());
            keys.add(s);
            keys.add(s.toLowerCase());
            keys.add(s.toUpperCase());
            keys.add("idea_" + s);
            keys.add(("idea_" + s).toLowerCase());
            keys.add(("idea_" + s).toUpperCase());
        }

        if (s.endsWith("_speed") || s.endsWith("_SPEED")) {
            s = s.substring(0, s.length() - 6);
            keys.add("modifier_" + s);
            keys.add(("modifier_" + s).toLowerCase());
            keys.add(("modifier_" + s).toUpperCase());
            keys.add(s);
            keys.add(s.toLowerCase());
            keys.add(s.toUpperCase());
            keys.add("idea_" + s);
            keys.add(("idea_" + s).toLowerCase());
            keys.add(("idea_" + s).toUpperCase());
        }

        return keys;
    }
}

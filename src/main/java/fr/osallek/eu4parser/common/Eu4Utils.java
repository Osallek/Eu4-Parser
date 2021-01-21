package fr.osallek.eu4parser.common;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.eu4parser.model.game.Building;
import fr.osallek.eu4parser.model.save.country.Country;
import org.apache.commons.collections4.iterators.ReverseListIterator;

import java.text.Collator;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class Eu4Utils {

    private Eu4Utils() {}

    public static final Collator COLLATOR = Collator.getInstance();

    public static final String MAGIC_WORD = "EU4txt";

    public static final String AI_FILE = "ai";

    public static final String GAMESTATE_FILE = "gamestate";

    public static final String META_FILE = "meta";

    public static final LocalDate DEFAULT_DATE = LocalDate.of(1, 1, 1);;

    public static final String DEFAULT_TAG = "---";

    public static final String DEFAULT_TAG_QUOTES = "\"" + DEFAULT_TAG + "\"";

    public static final String IMPASSABLE_CLIMATE = "impassable";

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

    static {
        COLLATOR.setStrength(Collator.NO_DECOMPOSITION);
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

    public static Integer cleanStringAndParseToDouble(String s) {
        return Integer.parseInt(s.replaceAll("[\\D.]", ""));
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
}

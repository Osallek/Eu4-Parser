package com.osallek.eu4parser.common;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.eu4parser.model.game.Building;
import com.osallek.eu4parser.model.save.country.Country;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class Eu4Utils {

    private Eu4Utils() {}

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
            iterator = queue.iterator();
            while (iterator.hasNext()) {
                Building building = iterator.next();
                for (List<Building> buildingList : tree) {
                    if (buildingList.get(buildingList.size() - 1).getName().equals(building.getMakeObsolete())) {
                        buildingList.add(building);
                        iterator.remove();
                    }
                }
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

package com.osallek.eu4parser.common;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.eu4parser.model.game.Building;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public final class Eu4Utils {

    private Eu4Utils() {}

    public static final String MAGIC_WORD = "EU4txt";

    public static final String AI_FILE = "ai";

    public static final String GAMESTATE_FILE = "gamestate";

    public static final String META_FILE = "meta";

    public static final Date DEFAULT_DATE;

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

    static {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1, Calendar.JANUARY, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        DEFAULT_DATE = calendar.getTime();
    }

    public static Date stringToDate(String s) {
        try {
            return ClausewitzUtils.stringToDate(s);
        } catch (ParseException e) {
            return null;
        }
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
}

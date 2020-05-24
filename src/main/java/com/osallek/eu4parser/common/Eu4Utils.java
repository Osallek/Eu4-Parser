package com.osallek.eu4parser.common;

import com.osallek.clausewitzparser.common.ClausewitzUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

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
}

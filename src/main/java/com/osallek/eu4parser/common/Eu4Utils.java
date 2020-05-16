package com.osallek.eu4parser.common;

import com.osallek.clausewitzparser.common.ClausewitzUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public final class Eu4Utils {

    private Eu4Utils() {}

    public static final Date DEFAULT_DATE;

    public static final String DEFAULT_TAG = "---";

    public static final String DEFAULT_TAG_QUOTES = "\"" + DEFAULT_TAG + "\"";

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

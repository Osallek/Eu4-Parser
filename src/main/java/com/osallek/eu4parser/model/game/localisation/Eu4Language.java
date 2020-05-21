package com.osallek.eu4parser.model.game.localisation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum Eu4Language {
    ENGLISH("_l_english", Locale.ENGLISH),
    FRENCH("_l_french", Locale.FRENCH),
    GERMAN("_l_german", Locale.GERMAN),
    SPANISH("_l_spanish", new Locale("es"));

    public final String fileEndWith;

    public final Locale locale;

    Eu4Language(String fileEndWith, Locale locale) {
        this.fileEndWith = fileEndWith;
        this.locale = locale;
    }

    private static final Map<String, Eu4Language> BY_LANGUAGE = new HashMap<>();

    static {
        Arrays.stream(values()).forEach(eu4Language -> BY_LANGUAGE.put(eu4Language.locale.getLanguage(), eu4Language));
    }

    public Eu4Language getByLocale(Locale locale) {
        return BY_LANGUAGE.getOrDefault(locale.getLanguage(), ENGLISH);
    }
}

package fr.osallek.eu4parser.model.game.localisation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum Eu4Language {
    ENGLISH("l_english", Locale.ENGLISH),
    FRENCH("l_french", Locale.FRENCH),
    GERMAN("l_german", Locale.GERMAN),
    SPANISH("l_spanish", new Locale("es"));

    public final String fileEndWith;

    public final Locale locale;

    Eu4Language(String fileEndWith, Locale locale) {
        this.fileEndWith = fileEndWith;
        this.locale = locale;
    }

    private static final Map<String, Eu4Language> BY_LANGUAGE = new HashMap<>();
    private static final Map<String, Eu4Language> BY_END_WITH = new HashMap<>();

    static {
        Arrays.stream(values()).forEach(eu4Language -> BY_LANGUAGE.put(eu4Language.locale.getLanguage(), eu4Language));
        Arrays.stream(values()).forEach(eu4Language -> BY_END_WITH.put(eu4Language.fileEndWith, eu4Language));
    }

    public static Eu4Language getByLocale(Locale locale) {
        return BY_LANGUAGE.getOrDefault(locale.getLanguage(), ENGLISH);
    }

    public static Eu4Language getByEndWith(String fileEndWith) {
        return BY_END_WITH.getOrDefault(fileEndWith, ENGLISH);
    }

    public static Eu4Language getDefault() {
        return getByLocale(Locale.getDefault());
    }
}

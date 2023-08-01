package fr.osallek.eu4parser.model;

import java.util.regex.Pattern;

public enum ModType {
    LOCAL(Pattern.compile(".*")), STEAM(Pattern.compile("^ugc_\\d+(.mod)?$")), PDX(Pattern.compile("^pdx_\\d+(.mod)?$"));

    public final Pattern pattern;

    ModType(Pattern pattern) {
        this.pattern = pattern;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public static ModType getByFileName(String fileName) {
        if (PDX.pattern.matcher(fileName).matches()) {
            return PDX;
        } else if (STEAM.pattern.matcher(fileName).matches()) {
            return STEAM;
        } else {
            return LOCAL;
        }
    }
}

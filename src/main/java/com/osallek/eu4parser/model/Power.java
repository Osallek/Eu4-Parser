package com.osallek.eu4parser.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum Power {
    ADM, DIP, MIL;

    private static final Map<String, Power> BY_NAME = new HashMap<>();

    static {
        Arrays.stream(values()).forEach(power -> BY_NAME.put(power.name().toLowerCase(), power));
    }

    public static Power byName(String s) {
        return BY_NAME.get(s.toLowerCase());
    }
}

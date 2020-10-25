package com.osallek.eu4parser.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum Power {
    ADM(3), DIP(2), MIL(1);

    private static final Map<String, Power> BY_NAME = new HashMap<>();
    private static final Map<Integer, Power> BY_PARLIAMENT_TYPE = new HashMap<>();

    static {
        Arrays.stream(values()).forEach(power -> BY_NAME.put(power.name().toLowerCase(), power));
        Arrays.stream(values()).forEach(power -> BY_PARLIAMENT_TYPE.put(power.parliamentType, power));
    }

    public final int parliamentType;

    Power(int parliamentType) {
        this.parliamentType = parliamentType;
    }

    public static Power byName(String s) {
        return BY_NAME.get(s.toLowerCase());
    }

    public static Power byParliamentType(int i) {
        return BY_PARLIAMENT_TYPE.get(i);
    }
}

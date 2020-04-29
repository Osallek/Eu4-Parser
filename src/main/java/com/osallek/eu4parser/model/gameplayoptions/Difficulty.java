package com.osallek.eu4parser.model.gameplayoptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum Difficulty {
    VERY_EASY(-1),
    EASY(0),
    NORMAL(1),
    HARD(2),
    VERY_HARD(3);

    public final int value;

    Difficulty(int value) {
        this.value = value;
    }

    private static final Map<Integer, Difficulty> BY_VALUE = new HashMap<>();

    static {
        Arrays.stream(values()).forEach(difficulty -> BY_VALUE.put(difficulty.value, difficulty));
    }

    public static Difficulty ofValue(Integer value) {
        return BY_VALUE.get(value);
    }
}

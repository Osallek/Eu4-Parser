package com.osallek.eu4parser.common;

import com.osallek.clausewitzparser.common.ClausewitzUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ModifiersUtils {

    private ModifiersUtils() {}

    private static final Map<String, StaticModifiers.ModifierType> MODIFIERS_MAP = new HashMap<>();

    static {
        Arrays.stream(StaticModifiers.values()).forEach(staticModifier -> MODIFIERS_MAP.put(staticModifier.name(), staticModifier.type));
    }

    public static void addModifier(String name, StaticModifiers.ModifierType type) {
        MODIFIERS_MAP.put(name.toUpperCase(), type);
    }

    public static StaticModifiers.ModifierType getType(String name) {
        return MODIFIERS_MAP.get(ClausewitzUtils.removeQuotes(name).toUpperCase());
    }

    /**
     * For constants (i.e. = yes) return 1 for yes, 0 for false
     */
    public static double getSum(double value, String name, String... values) {
        StaticModifiers.ModifierType type = getType(name);

        if (type == null) {
            return value;
        }

        switch (type) {
            case ADDITIVE:
                return value + Arrays.stream(values).map(NumbersUtils::toDouble).filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum();
            case MULTIPLICATIVE:
                return value * (1 + Arrays.stream(values).map(NumbersUtils::toDouble).filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum());
            case CONSTANT:
                return (value == 1 || Arrays.stream(values).anyMatch("yes"::equalsIgnoreCase)) ? 1 : 0;
            default:
                return value;
        }
    }
}

package com.osallek.eu4parser.common;

import com.osallek.clausewitzparser.common.ClausewitzUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ModifiersUtils {

    private ModifiersUtils() {}

    private static final Map<String, Modifiers.ModifierType> MODIFIERS_MAP = new HashMap<>();

    static {
        Arrays.stream(Modifiers.values()).forEach(staticModifier -> MODIFIERS_MAP.put(staticModifier.name(), staticModifier.type));
    }

    public static void addModifier(String name, Modifiers.ModifierType type) {
        MODIFIERS_MAP.put(name.toUpperCase(), type);
    }

    public static Modifiers.ModifierType getType(String name) {
        return MODIFIERS_MAP.get(ClausewitzUtils.removeQuotes(name).toUpperCase());
    }

    /**
     * For constants (i.e. = yes) return 1 for yes, 0 for false
     */
    public static double getSum(double value, String name, String... values) {
        Modifiers.ModifierType type = getType(name);

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

    public static Map<String, List<String>> scaleModifiers(Map<String, List<String>> modifiers, Number scale) {
        scale = NumbersUtils.numberOrDefault(scale);
        double finalScale = scale.doubleValue();
        Map<String, List<String>> toReturn = new HashMap<>(modifiers);
        toReturn.replaceAll((key, value) -> {
            if (Modifiers.ModifierType.ADDITIVE.equals(ModifiersUtils.getType(key))
                || Modifiers.ModifierType.MULTIPLICATIVE.equals(ModifiersUtils.getType(key))) {
                return value.stream()
                            .map(NumbersUtils::toDouble)
                            .map(v -> BigDecimal.valueOf(v).multiply(BigDecimal.valueOf(finalScale)).doubleValue())
                            .map(ClausewitzUtils::doubleToString)
                            .collect(Collectors.toList());
            }

            return value;
        });

        return toReturn;
    }
}

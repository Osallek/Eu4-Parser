package com.osallek.eu4parser.common;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.eu4parser.model.game.Modifiers;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ModifiersUtils {

    private ModifiersUtils() {}

    private static final Map<String, Modifier.ModifierType> MODIFIERS_MAP = new HashMap<>();

    static {
        Arrays.stream(Modifier.values()).forEach(staticModifier -> MODIFIERS_MAP.put(staticModifier.name(), staticModifier.type));
    }

    public static void addModifier(String name, Modifier.ModifierType type) {
        MODIFIERS_MAP.put(name.toUpperCase(), type);
    }

    public static Modifier.ModifierType getType(String name) {
        return MODIFIERS_MAP.get(ClausewitzUtils.removeQuotes(name).toUpperCase());
    }

    /**
     * For constants (i.e. = yes) return 1 for yes, 0 for false
     */
    public static double getSum(double value, String name, Modifiers... modifiers) {
        Modifier.ModifierType type = getType(name);

        if (type == null) {
            return value;
        }

        switch (type) {
            case ADDITIVE:
                return value + Arrays.stream(modifiers)
                                     .map(m -> m.getModifier(name))
                                     .filter(Objects::nonNull)
                                     .map(NumbersUtils::toDouble)
                                     .filter(Objects::nonNull)
                                     .mapToDouble(Double::doubleValue)
                                     .sum();
            case MULTIPLICATIVE:
                return value * (1 + Arrays.stream(modifiers)
                                          .map(m -> m.getModifier(name))
                                          .filter(Objects::nonNull)
                                          .map(NumbersUtils::toDouble)
                                          .filter(Objects::nonNull)
                                          .mapToDouble(Double::doubleValue)
                                          .sum());
            case CONSTANT:
                return (value == 1 || Arrays.stream(modifiers).map(m -> m.getModifier(name)).filter(Objects::nonNull).anyMatch("yes"::equalsIgnoreCase)) ? 1
                                                                                                                                                         : 0;
            default:
                return value;
        }
    }

    public static Modifiers scaleModifiers(Modifiers modifiers, Number scale) {
        scale = NumbersUtils.numberOrDefault(scale);
        double finalScale = scale.doubleValue();
        Modifiers toReturn = Modifiers.copy(modifiers);

        toReturn.getModifiers().replaceAll((key, value) -> {
            if (Modifier.ModifierType.ADDITIVE.equals(ModifiersUtils.getType(key)) ||
                Modifier.ModifierType.MULTIPLICATIVE.equals(ModifiersUtils.getType(key))) {
                return ClausewitzUtils.doubleToString(BigDecimal.valueOf(NumbersUtils.toDouble(value)).multiply(BigDecimal.valueOf(finalScale)).doubleValue());
            }

            return value;
        });

        return toReturn;
    }

    public static Modifiers sumModifiers(Modifiers... modifiers) {
        if (modifiers.length > 0) {
            Set<String> enables = Arrays.stream(modifiers).map(Modifiers::getEnables).flatMap(Collection::stream).collect(Collectors.toSet());
            Map<String, String> modifier = Arrays.stream(modifiers)
                                                 .map(Modifiers::getModifiers)
                                                 .map(Map::entrySet)
                                                 .flatMap(Collection::stream)
                                                 .collect(Collectors.groupingBy(Map.Entry::getKey,
                                                                                Collectors.mapping(Map.Entry::getValue, Collectors.toList())))
                                                 .entrySet()
                                                 .stream()
                                                 .filter(entry -> CollectionUtils.isNotEmpty(entry.getValue()))
                                                 .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                                                     Modifier.ModifierType type = getType(entry.getKey());

                                                     if (type == null) {
                                                         return entry.getValue().get(0);
                                                     }

                                                     switch (type) {
                                                         case ADDITIVE:
                                                         case MULTIPLICATIVE:
                                                             return ClausewitzUtils.doubleToString(entry.getValue()
                                                                                                        .stream()
                                                                                                        .filter(Objects::nonNull)
                                                                                                        .map(NumbersUtils::toDouble)
                                                                                                        .filter(Objects::nonNull)
                                                                                                        .mapToDouble(Double::doubleValue)
                                                                                                        .sum());
                                                         case CONSTANT:
                                                             return entry.getValue().stream().filter(Objects::nonNull).anyMatch("yes"::equalsIgnoreCase) ? "yes"
                                                                                                                                                         : "no";
                                                     }

                                                     return entry.getValue().get(0);
                                                 }));

            return new Modifiers(enables, modifier);
        }

        return new Modifiers();
    }

    public static void sumModifiers(String name, String value, Modifiers modifiers) {
        name = ClausewitzUtils.removeQuotes(name).toLowerCase();
        Modifier.ModifierType type = getType(name);

        if (type == null) {
            return;
        }

        switch (type) {
            case ADDITIVE:
            case MULTIPLICATIVE:
                modifiers.getModifiers().put(name, ClausewitzUtils.doubleToString(NumbersUtils.toDouble(modifiers.getModifiers().getOrDefault(name, "0"))
                                                                                  + NumbersUtils.doubleOrDefault(NumbersUtils.toDouble(value))));
                break;
            case CONSTANT:
                modifiers.getModifiers()
                         .put(name,
                              ("yes".equalsIgnoreCase(modifiers.getModifiers().getOrDefault(name, value)) || "yes".equalsIgnoreCase(value)) ? "yes" : "no");
                break;
        }
    }

    public static <T, K, V> Collector<T, ?, Map<K, V>> toMap(Function<? super T, ? extends K> keyMapper,
                                                             Function<? super T, ? extends V> valueMapper,
                                                             Predicate<? super K> useOlder) {
        return Collector.of(HashMap::new,
                            (m, t) -> {
                                K k = keyMapper.apply(t);
                                m.merge(k, valueMapper.apply(t), (a, b) -> useOlder.test(k) ? a : b);
                            },
                            (m1, m2) -> {
                                m2.forEach((k, v) -> m1.merge(k, v, (a, b) -> useOlder.test(k) ? a : b));
                                return m1;
                            });
    }
}

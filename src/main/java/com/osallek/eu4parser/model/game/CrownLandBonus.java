package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CrownLandBonus implements Comparable<CrownLandBonus> {

    private final String name;

    private final Double rangeFrom;

    private final Double rangeTo;

    private final Map<String, List<String>> modifiers;

    public CrownLandBonus(ClausewitzItem item) {
        this.name = item.getVarAsString("key");
        this.rangeFrom = item.getVarAsDouble("range_from");
        this.rangeTo = item.getVarAsDouble("range_to");

        ClausewitzItem child = item.getChild("modifier");
        this.modifiers = child == null ? null : child.getVariables()
                                                     .stream()
                                                     .collect(Collectors.groupingBy(ClausewitzVariable::getName,
                                                                                    LinkedHashMap::new,
                                                                                    Collectors.mapping(ClausewitzVariable::getValue, Collectors.toList())));
    }

    public String getName() {
        return name;
    }

    public Double getRangeFrom() {
        return rangeFrom;
    }

    public Double getRangeTo() {
        return rangeTo;
    }

    public Map<String, List<String>> getModifiers() {
        return modifiers;
    }

    public boolean isInRange(double range) {
        return (this.rangeFrom == null || this.rangeFrom <= range) && (this.rangeTo == null || this.rangeTo > range);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof CrownLandBonus)) {
            return false;
        }

        CrownLandBonus area = (CrownLandBonus) o;

        return Objects.equals(name, area.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(@NotNull CrownLandBonus o) {
        return Comparator.comparingDouble(CrownLandBonus::getRangeFrom).compare(this, o);
    }
}

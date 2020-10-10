package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzObject;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.model.Power;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Technology implements Comparable<Technology> {

    private final Power type;

    private final Map<String, List<String>> aheadOfTime;

    private final int year;

    private final Map<String, List<String>> modifiers;

    public Technology(ClausewitzItem item, Power power, Map<String, List<String>> aheadOfTime) {
        this.type = power;
        this.aheadOfTime = aheadOfTime;
        this.year = item.getVarAsInt("year");

        List<ClausewitzVariable> list = item.getVarsNot("year");
        this.modifiers = list.stream()
                             .collect(Collectors.groupingBy(ClausewitzObject::getName, Collectors.mapping(ClausewitzVariable::getValue, Collectors.toList())));
    }

    public Power getType() {
        return type;
    }

    public Map<String, List<String>> getAheadOfTime() {
        return aheadOfTime;
    }

    public int getYear() {
        return year;
    }

    public Map<String, List<String>> getModifiers() {
        return modifiers;
    }

    @Override
    public int compareTo(@NotNull Technology o) {
        return Comparator.comparingInt(Technology::getYear).compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Technology that = (Technology) o;
        return year == that.year &&
               type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, year);
    }

    @Override
    public String toString() {
        return type + " (" + year + ')';
    }
}

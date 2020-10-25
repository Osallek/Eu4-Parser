package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Isolationism implements Comparable<Isolationism> {

    private final String name;

    private String localizedName;

    private final int isolationValue;

    private final Map<String, List<String>> modifiers;

    public Isolationism(ClausewitzItem item) {
        this.name = item.getName();
        this.isolationValue = item.getVarAsInt("isolation_value");

        ClausewitzItem child = item.getChild("modifier");
        this.modifiers = child == null ? null : child.getVariables()
                                                     .stream()
                                                     .collect(Collectors.groupingBy(ClausewitzVariable::getName,
                                                                                    Collectors.mapping(ClausewitzVariable::getValue, Collectors.toList())));
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public int getIsolationValue() {
        return isolationValue;
    }

    public Map<String, List<String>> getModifiers() {
        return modifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Isolationism)) {
            return false;
        }

        Isolationism area = (Isolationism) o;

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
    public int compareTo(@NotNull Isolationism o) {
        return Comparator.comparingInt(Isolationism::getIsolationValue).compare(this, o);
    }
}

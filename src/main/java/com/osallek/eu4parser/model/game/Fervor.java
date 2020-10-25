package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Fervor implements Comparable<Fervor> {

    private final String name;

    private String localizedName;

    private final int cost;

    private final Map<String, List<String>> modifiers;

    public Fervor(ClausewitzItem item) {
        this.name = item.getName();
        this.cost = item.getVarAsInt("cost");

        ClausewitzItem child = item.getChild("effect");
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

    public int getCost() {
        return cost;
    }

    public Map<String, List<String>> getModifiers() {
        return modifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Fervor)) {
            return false;
        }

        Fervor area = (Fervor) o;

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
    public int compareTo(@NotNull Fervor o) {
        return Comparator.comparingInt(Fervor::getCost).compare(this, o);
    }
}

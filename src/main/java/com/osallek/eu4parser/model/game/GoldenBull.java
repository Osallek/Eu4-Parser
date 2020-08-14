package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class GoldenBull {

    private final String name;

    private String localizedName;

    private List<String> mechanics;

    private Map<String, Double> modifiers;

    public GoldenBull(ClausewitzItem item) {
        this.name = item.getName();

        ClausewitzItem child = item.getChild("modifier");
        this.modifiers = child == null ? null : child.getVariables()
                                                     .stream()
                                                     .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                               ClausewitzVariable::getAsDouble,
                                                                               (a, b) -> b,
                                                                               LinkedHashMap::new));
        ClausewitzList list = item.getList("mechanics");
        this.mechanics = list == null ? null : list.getValues();
    }

    public GoldenBull(GoldenBull other) {
        this.name = other.name;
        this.localizedName = other.localizedName;
        this.mechanics = other.mechanics;
        this.modifiers = other.modifiers;
    }

    public GoldenBull(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public List<String> getMechanics() {
        return this.mechanics == null ? new ArrayList<>() : this.mechanics;
    }

    public void setMechanics(List<String> mechanics) {
        this.mechanics = mechanics;
    }

    public Map<String, Double> getModifiers() {
        return this.modifiers == null ? new LinkedHashMap<>() : this.modifiers;
    }

    public void addModifier(String modifier, Double quantity) {
        if (modifier == null) {
            this.modifiers = new LinkedHashMap<>();
        }

        this.modifiers.put(modifier, quantity);
    }

    public void removeModifier(String modifier) {
        if (this.modifiers != null) {
            this.modifiers.remove(modifier);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null && getName() == null) {
            return true;
        }

        if (!(o instanceof GoldenBull)) {
            return false;
        }
        GoldenBull decree = (GoldenBull) o;
        return Objects.equals(getName(), decree.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}

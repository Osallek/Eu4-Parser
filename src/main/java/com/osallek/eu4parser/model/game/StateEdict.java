package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.eu4parser.model.Color;

import java.util.Objects;

public class StateEdict {

    private final String name;

    private String localizedName;

    private final Color color;

    private final Condition potential;

    private final Condition allow;

    private final Modifiers modifiers;


    public StateEdict(ClausewitzItem item) {
        this.name = item.getName();
        this.modifiers = new Modifiers(item.getChild("modifier"));

        ClausewitzItem child = item.getChild("potential");
        this.potential = child == null ? null : new Condition(child);

        child = item.getChild("allow");
        this.allow = child == null ? null : new Condition(child);

        ClausewitzList list = item.getList("color");
        this.color = list == null ? null : new Color(list);
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public Condition getPotential() {
        return potential;
    }

    public Condition getAllow() {
        return allow;
    }

    public Modifiers getModifiers() {
        return modifiers;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof StateEdict)) {
            return false;
        }

        StateEdict ageAbility = (StateEdict) o;

        return Objects.equals(name, ageAbility.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}

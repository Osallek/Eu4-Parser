package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.model.Power;

import java.util.Objects;

public class Policy {

    private final String name;

    private String localizedName;

    private final Power category;

    private final Condition potential;

    private final Condition allow;

    private final Modifiers modifiers;

    public Policy(ClausewitzItem item) {
        this.name = item.getName();
        ClausewitzVariable var = item.getVar("monarch_power");
        this.category = var == null ? null : Power.valueOf(var.getValue().toUpperCase());
        this.modifiers = new Modifiers(item.getVarsNot("monarch_power"));

        ClausewitzItem child = item.getChild("potential");
        this.potential = child == null ? null : new Condition(child);

        child = item.getChild("allow");
        this.allow = child == null ? null : new Condition(child);
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

    public Power getCategory() {
        return category;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Policy)) {
            return false;
        }

        Policy area = (Policy) o;

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
}

package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.Power;

import java.util.Objects;

public class ParliamentIssue {

    private final String name;

    private String localizedName;

    private final Power category;

    private final Condition allow;

    private final Modifiers modifiers;

    public ParliamentIssue(ClausewitzItem item) {
        this.name = item.getName();
        this.category = Power.byParliamentType(item.getVarAsInt("category"));
        this.modifiers = new Modifiers(item.getChild("modifier"));

        ClausewitzItem child = item.getChild("allow");
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

    public Condition getAllow() {
        return allow;
    }

    public Modifiers getModifiers() {
        return modifiers;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ParliamentIssue that = (ParliamentIssue) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

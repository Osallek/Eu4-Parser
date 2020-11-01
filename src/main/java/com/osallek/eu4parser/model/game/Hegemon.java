package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Objects;

public class Hegemon {

    private final String name;

    private String localizedName;

    private final Condition allow;

    private final Modifiers base;

    private final Modifiers scale;

    private final Modifiers max;

    public Hegemon(ClausewitzItem item) {
        this.name = item.getName();

        this.base = new Modifiers(item.getChild("base"));

        this.scale = new Modifiers(item.getChild("scale"));

        this.max = new Modifiers(item.getChild("max"));

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

    public Condition getAllow() {
        return allow;
    }

    public Modifiers getBase() {
        return base;
    }

    public Modifiers getScale() {
        return scale;
    }

    public Modifiers getMax() {
        return max;
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

        Hegemon that = (Hegemon) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

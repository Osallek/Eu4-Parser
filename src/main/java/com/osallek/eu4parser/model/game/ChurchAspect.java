package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Objects;

public class ChurchAspect {

    private final String name;

    private String localizedName;

    private final Modifiers modifiers;

    private final Integer cost;

    public ChurchAspect(ClausewitzItem item) {
        this.name = item.getName();
        this.cost  = item.getVarAsInt("cost");
        this.modifiers = new Modifiers(item.getChild("modifier"));
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

    public Modifiers getModifiers() {
        return modifiers;
    }

    public Integer getCost() {
        return cost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ChurchAspect)) {
            return false;
        }

        ChurchAspect that = (ChurchAspect) o;
        return Objects.equals(name, that.name);
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

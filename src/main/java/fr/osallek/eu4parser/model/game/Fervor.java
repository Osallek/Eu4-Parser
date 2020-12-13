package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Objects;

public class Fervor {

    private final String name;

    private String localizedName;

    private final int cost;

    private final Modifiers modifiers;

    public Fervor(ClausewitzItem item) {
        this.name = item.getName();
        this.cost = item.getVarAsInt("cost");
        this.modifiers = new Modifiers(item.getChild("effect"));
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

    public Modifiers getModifiers() {
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
}

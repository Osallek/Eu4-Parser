package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Comparator;
import java.util.Objects;

public class Isolationism implements Comparable<Isolationism> {

    private final String name;

    private String localizedName;

    private final int isolationValue;

    private final Modifiers modifiers;

    public Isolationism(ClausewitzItem item) {
        this.name = item.getName();
        this.isolationValue = item.getVarAsInt("isolation_value");
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

    public int getIsolationValue() {
        return isolationValue;
    }

    public Modifiers getModifiers() {
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
    public int compareTo(Isolationism o) {
        return Comparator.comparingInt(Isolationism::getIsolationValue).compare(this, o);
    }
}

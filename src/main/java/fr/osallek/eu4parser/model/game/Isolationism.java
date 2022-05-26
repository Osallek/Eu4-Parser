package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Comparator;
import java.util.Objects;

public class Isolationism implements Comparable<Isolationism> {

    private final ClausewitzItem item;

    public Isolationism(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public int getIsolationValue() {
        return this.item.getVarAsInt("isolation_value");
    }

    public void setIsolationValue(int isolationValue) {
        this.item.setVariable("isolation_value", isolationValue);
    }

    public Modifiers getModifiers() {
        return new Modifiers(this.item.getChild("modifier"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Isolationism isolationism)) {
            return false;
        }

        return Objects.equals(getName(), isolationism.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(Isolationism o) {
        return Comparator.comparingInt(Isolationism::getIsolationValue).compare(this, o);
    }
}

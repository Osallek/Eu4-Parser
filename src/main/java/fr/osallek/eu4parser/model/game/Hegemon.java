package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Objects;

public class Hegemon {

    private final ClausewitzItem item;

    public Hegemon(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public ConditionAnd getAllow() {
        ClausewitzItem child = item.getChild("allow");
        return child == null ? null : new ConditionAnd(child);
    }

    public Modifiers getBase() {
        return new Modifiers(item.getChild("base"));
    }

    public Modifiers getScale() {
        return new Modifiers(item.getChild("scale"));
    }

    public Modifiers getMax() {
        return new Modifiers(item.getChild("max"));
    }

    @Override
    public String toString() {
        return getName();
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
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}

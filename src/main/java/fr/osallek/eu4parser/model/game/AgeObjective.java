package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Objects;

public class AgeObjective {

    private final ClausewitzItem item;

    public AgeObjective(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public ConditionAnd getAllow() {
        ClausewitzItem child = this.item.getChild("allow");
        return child == null ? null : new ConditionAnd(child);
    }

    public ConditionAnd getTrigger() {
        return new ConditionAnd(this.item, "allow");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof AgeObjective)) {
            return false;
        }

        AgeObjective ageObjective = (AgeObjective) o;

        return Objects.equals(getName(), ageObjective.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return getName();
    }
}

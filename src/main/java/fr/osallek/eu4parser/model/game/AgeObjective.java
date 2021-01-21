package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Objects;

public class AgeObjective {
    private final String name;

    private final Condition allow;

    private final Condition trigger;

    private String localizedName;

    public AgeObjective(ClausewitzItem item) {
        this.name = item.getName();

        ClausewitzItem child = item.getChild("allow");
        this.allow = child == null ? null : new Condition(child);

        item.removeChild("allow");
        this.trigger = new Condition(item);
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

    public Condition getAllow() {
        return allow;
    }

    public Condition getTrigger() {
        return trigger;
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

        return Objects.equals(name, ageObjective.name);
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

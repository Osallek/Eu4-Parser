package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Objects;

public class Names {

    private final String name;

    private final Condition trigger;

    public Names(ClausewitzItem item) {
        this.name = item.getVarAsString("name");

        ClausewitzItem child = item.getChild("trigger");
        this.trigger = child == null ? null : new Condition(child);
    }

    public String getName() {
        return name;
    }

    public Condition getTrigger() {
        return trigger;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Names)) {
            return false;
        }

        Names names = (Names) o;

        return Objects.equals(name, names.name);
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

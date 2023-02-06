package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import java.util.Objects;

public class ParliamentBribe {

    private final ClausewitzItem item;

    private final Game game;

    public ParliamentBribe(ClausewitzItem item, Game game) {
        this.item = item;
        this.game = game;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public ConditionAnd getTrigger() {
        ClausewitzItem child = this.item.getChild("trigger");
        return child == null ? null : new ConditionAnd(child);
    }

    public Game getGame() {
        return game;
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

        if (!(o instanceof ParliamentBribe parliamentBribe)) {
            return false;
        }

        return Objects.equals(getName(), parliamentBribe.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}

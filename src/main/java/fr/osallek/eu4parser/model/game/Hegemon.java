package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;

import java.util.Objects;
import java.util.Optional;

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

    public Optional<ConditionAnd> getAllow() {
        return this.item.getChild("allow").map(ConditionAnd::new);
    }

    public Optional<Modifiers> getBase() {
        return this.item.getChild("base").map(Modifiers::new);
    }

    public Optional<Modifiers> getScale() {
        return this.item.getChild("scale").map(Modifiers::new);
    }

    public Optional<Modifiers> getMax() {
        return this.item.getChild("max").map(Modifiers::new);
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

        if (!(o instanceof Hegemon hegemon)) {
            return false;
        }

        return Objects.equals(getName(), hegemon.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}

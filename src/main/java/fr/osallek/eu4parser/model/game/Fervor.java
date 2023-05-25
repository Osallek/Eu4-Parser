package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Objects;
import java.util.Optional;

public class Fervor {

    private final ClausewitzItem item;

    public Fervor(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public int getCost() {
        return this.item.getVarAsInt("cost").orElse(0);
    }

    public void setCost(int cost) {
        this.item.setVariable("cost", cost);
    }

    public Optional<Modifiers> getModifiers() {
        return this.item.getChild("effect").map(Modifiers::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Fervor fervor)) {
            return false;
        }

        return Objects.equals(getName(), fervor.getName());
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

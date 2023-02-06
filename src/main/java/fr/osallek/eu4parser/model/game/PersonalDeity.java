package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Objects;

public class PersonalDeity {

    private final ClausewitzItem item;

    public PersonalDeity(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public int getSprite() {
        return this.item.getVarAsInt("sprite");
    }

    public void setSprite(int sprite) {
        this.item.setVariable("sprite", sprite);
    }

    public ConditionAnd getAllow() {
        ClausewitzItem child = this.item.getChild("allow");
        return child == null ? null : new ConditionAnd(this.item.getChild("allow"));
    }

    public Modifiers getModifiers() {
        return new Modifiers(this.item.getVarsNot("sprite"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof PersonalDeity personalDeity)) {
            return false;
        }

        return Objects.equals(getName(), personalDeity.getName());
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

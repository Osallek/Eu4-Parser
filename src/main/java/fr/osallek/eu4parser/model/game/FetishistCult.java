package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;

import java.util.Objects;

public class FetishistCult {

    private final ClausewitzItem item;

    public FetishistCult(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Modifiers getModifiers() {
        return new Modifiers(item.getVarsNot("sprite"));
    }

    public ConditionAnd getAllow() {
        ClausewitzItem child = this.item.getChild("allow");

        if (child == null) {
            return null;
        } else {
            ConditionAnd allow = new ConditionAnd(child);
            allow.removeCondition("has_unlocked_cult", this.getName()); //Prevent endless recursive

            return allow;
        }
    }

    public int getSprite() {
        return this.item.getVarAsInt("sprite");
    }

    public void setSprite(int sprite) {
        this.item.setVariable("sprite", sprite);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof FetishistCult fetishistCult)) {
            return false;
        }

        return Objects.equals(getName(), fetishistCult.getName());
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

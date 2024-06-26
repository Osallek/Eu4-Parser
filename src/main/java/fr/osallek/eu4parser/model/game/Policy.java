package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.Power;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;

import java.util.Objects;

public class Policy {

    private final ClausewitzItem item;

    public Policy(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Power getCategory() {
        return Power.byName(this.item.getVarAsString("monarch_power"));
    }

    public void setCategory(Power category) {
        this.item.setVariable("monarch_power", category.name());
    }

    public ConditionAnd getPotential() {
        ClausewitzItem child = item.getChild("potential");
        return child == null ? null : new ConditionAnd(child);
    }

    public void setPotential(ConditionAnd condition) {
        if (condition == null) {
            this.item.removeChild("potential");
            return;
        }

        ClausewitzItem child = this.item.getChild("potential");
        //Todo Condition => item
    }

    public ConditionAnd getAllow() {
        ClausewitzItem child = item.getChild("allow");
        return child == null ? null : new ConditionAnd(child);
    }

    public void setAllow(ConditionAnd condition) {
        if (condition == null) {
            this.item.removeChild("allow");
            return;
        }

        ClausewitzItem child = this.item.getChild("allow");
        //Todo Condition => item
    }

    public Modifiers getModifiers() {
        return new Modifiers(this.item.getVarsNot("monarch_power"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Policy policy)) {
            return false;
        }

        return Objects.equals(getName(), policy.getName());
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

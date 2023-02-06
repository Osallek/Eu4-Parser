package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.Power;

import java.util.Objects;

public class ParliamentIssue extends GameModifier {

    public ParliamentIssue(ClausewitzItem item) {
        super(item);
    }

    @Override
    public Modifiers getModifier() {
        return new Modifiers(this.item.getChild("modifier"));
    }

    public Power getCategory() {
        return Power.byParliamentType(this.item.getVarAsInt("category"));
    }

    public void setCategory(Power category) {
        this.item.setVariable("category", category.name());
    }

    public ConditionAnd getAllow() {
        ClausewitzItem child = this.item.getChild("allow");
        return child == null ? null : new ConditionAnd(child);
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

        ParliamentIssue parliamentIssue = (ParliamentIssue) o;

        return Objects.equals(getName(), parliamentIssue.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}

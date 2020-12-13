package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.Power;

import java.util.Objects;

public class ParliamentIssue extends GameModifier {

    private final Power category;

    private final Condition allow;

    public ParliamentIssue(ClausewitzItem item) {
        super(item, new Modifiers(item.getChild("modifier")));
        this.category = Power.byParliamentType(item.getVarAsInt("category"));

        ClausewitzItem child = item.getChild("allow");
        this.allow = child == null ? null : new Condition(child);
    }

    public Power getCategory() {
        return category;
    }

    public Condition getAllow() {
        return allow;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ParliamentIssue that = (ParliamentIssue) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

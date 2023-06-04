package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.Power;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;

import java.util.Objects;
import java.util.Optional;

public class ParliamentIssue extends GameModifier {

    public ParliamentIssue(ClausewitzItem item) {
        super(item);
    }

    @Override
    public Optional<Modifiers> getModifier() {
        return this.item.getChild("modifier").map(Modifiers::new);
    }

    public Power getCategory() {
        return this.item.getVarAsInt("category").map(Power::byParliamentType).get();
    }

    public void setCategory(Power category) {
        this.item.setVariable("category", category.name());
    }

    public Optional<ConditionAnd> getAllow() {
        return this.item.getChild("allow").map(ConditionAnd::new);
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

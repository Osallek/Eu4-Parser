package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Objects;
import java.util.Optional;

public class TriggeredModifier extends GameModifier {

    public TriggeredModifier(ClausewitzItem item) {
        super(item);
    }

    @Override
    public Optional<Modifiers> getModifier() {
        return Optional.of(this.item.getVariables()).filter(CollectionUtils::isNotEmpty).map(Modifiers::new);
    }

    public Optional<ConditionAnd> getPotential() {
        return this.item.getChild("potential").map(ConditionAnd::new);
    }

    public Optional<ConditionAnd> getTrigger() {
        return this.item.getChild("trigger").map(ConditionAnd::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof TriggeredModifier triggeredModifier)) {
            return false;
        }

        return Objects.equals(getName(), triggeredModifier.getName());
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

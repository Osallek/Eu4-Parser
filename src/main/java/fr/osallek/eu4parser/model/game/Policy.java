package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.Power;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Objects;
import java.util.Optional;

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
        return this.item.getVarAsString("monarch_power").map(Power::byName).get();
    }

    public void setCategory(Power category) {
        this.item.setVariable("monarch_power", category.name());
    }

    public Optional<ConditionAnd> getPotential() {
        return this.item.getChild("potential").map(ConditionAnd::new);
    }

    public Optional<ConditionAnd> getAllow() {
        return this.item.getChild("allow").map(ConditionAnd::new);
    }

    public Optional<Modifiers> getModifiers() {
        return Optional.of(this.item.getVarsNot("monarch_power")).filter(CollectionUtils::isNotEmpty).map(Modifiers::new);
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

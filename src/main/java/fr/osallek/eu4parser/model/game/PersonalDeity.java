package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Objects;
import java.util.Optional;

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

    public Optional<Integer> getSprite() {
        return this.item.getVarAsInt("sprite");
    }

    public void setSprite(int sprite) {
        this.item.setVariable("sprite", sprite);
    }

    public Optional<ConditionAnd> getAllow() {
        return this.item.getChild("allow").map(ConditionAnd::new);
    }

    public Optional<Modifiers> getModifiers() {
        return Optional.of(this.item.getVarsNot("sprite")).filter(CollectionUtils::isNotEmpty).map(Modifiers::new);
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

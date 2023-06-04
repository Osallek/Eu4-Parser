package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Objects;
import java.util.Optional;

public class EventModifier extends GameModifier {

    public EventModifier(ClausewitzItem item) {
        super(item);
    }

    @Override
    public Optional<Modifiers> getModifier() {
        return Optional.of(this.item.getVarsNot("picture")).filter(CollectionUtils::isNotEmpty).map(Modifiers::new);
    }

    public Optional<String> getPicture() {
        return this.item.getVarAsString("picture");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof EventModifier eventModifier)) {
            return false;
        }

        return Objects.equals(getName(), eventModifier.getName());
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

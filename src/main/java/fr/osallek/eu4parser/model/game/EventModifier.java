package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Objects;

public class EventModifier extends GameModifier {

    public EventModifier(ClausewitzItem item) {
        super(item);
    }

    @Override
    public Modifiers getModifier() {
        return new Modifiers(this.item.getVarsNot("picture"));
    }

    public String getPicture() {
        return this.item.getVarAsString("picture");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof EventModifier)) {
            return false;
        }

        EventModifier eventModifier = (EventModifier) o;

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
